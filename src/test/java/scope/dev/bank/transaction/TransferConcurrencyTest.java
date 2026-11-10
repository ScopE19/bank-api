package scope.dev.bank.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import scope.dev.bank.TestcontainersConfiguration;
import scope.dev.bank.account.Account;
import scope.dev.bank.common.Money;
import scope.dev.bank.support.TestData;
import scope.dev.bank.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class TransferConcurrencyTest {

    @Autowired TransferService transferService;
    @Autowired TestData data;

    @Test
    void parallelTransfersInOneDirectionNeverLoseMoney() throws Exception {
        User alice = data.newUser();
        User bob = data.newUser();
        Account a = data.newAccount(alice, "1000.00");
        Account b = data.newAccount(bob, "0.00");

        runInParallel(20, i -> transferService.transfer(
                alice.getId(), a.getAccountNumber(), b.getAccountNumber(), Money.of("10.00")));

        assertThat(data.balanceOf(a)).isEqualTo(Money.of("800.00"));
        assertThat(data.balanceOf(b)).isEqualTo(Money.of("200.00"));

    }

    @Test
    void parallelTransfersInBothDirectionsDoNotDeadlockAndConserveMoney() throws Exception {
        User alice = data.newUser();
        User bob = data.newUser();
        Account a = data.newAccount(alice, "500.00");
        Account b = data.newAccount(bob, "500.00");

        runInParallel(40, i -> {
            if (i % 2 == 0) {
                transferService.transfer(alice.getId(), a.getAccountNumber(), b.getAccountNumber(), Money.of("5.00"));
            } else {
                transferService.transfer(bob.getId(), b.getAccountNumber(), a.getAccountNumber(), Money.of("5.00"));
            }
        });

        assertThat(data.balanceOf(a).plus(data.balanceOf(b))).isEqualTo(Money.of("1000.00"));
        assertThat(data.balanceOf(a)).isEqualTo(Money.of("500.00"));
    }

    // helper: start N tasks at the same instant and fail if any of them fails
    private interface Task {
        void run(int index) throws Exception;
    }

    private void runInParallel(int count, Task task) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(count);
        CountDownLatch ready = new CountDownLatch(count);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < count; i++) {
                final int index = i;
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    go.await();
                    task.run(index);
                    return null;
                }));
            }
            ready.await();
            go.countDown();
            for (Future<?> future : futures) {
                future.get(60, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }
    }
}
