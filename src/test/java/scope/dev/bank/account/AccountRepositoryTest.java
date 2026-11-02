package scope.dev.bank.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import scope.dev.bank.TestcontainersConfiguration;
import scope.dev.bank.common.Money;
import scope.dev.bank.user.User;
import scope.dev.bank.user.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class AccountRepositoryTest {

    @Autowired AccountRepository accounts;
    @Autowired UserRepository users;
    @Autowired JdbcTemplate jdbc;

    private User newUser() {
        return users.save(new User("u-" + UUID.randomUUID() + "example.com", "hash", "Test User"));
    }

    private String randomNumber() {
        return String.format("%012d", java.util.concurrent.ThreadLocalRandom.current().nextLong(1_000_000_000_000L));
    }

    @Test
    void savesAndReloadsAccountWithNoMoneyIntactAndIncrementsVersion() {
        User owner = newUser();
        Account account = accounts.save(new Account(owner.getId(), randomNumber()));
        assertThat(account.getVersion()).isZero();

        account.deposit(Money.of("100.50"));
        accounts.save(account);

        Account reloaded = accounts.findById(account.getId()).orElseThrow();
        assertThat(reloaded.getBalance()).isEqualTo(Money.of("100.50"));
        assertThat(reloaded.getVersion()).isEqualTo(1L);
    }

    @Test
    void databaseItselfRefusesNegativeBalance() {
        User owner = newUser();
        Account account = accounts.save(new Account(owner.getId(), randomNumber()));

        assertThatThrownBy(() ->
                jdbc.update("update account set balance = -1 where id = ?", account.getId()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
