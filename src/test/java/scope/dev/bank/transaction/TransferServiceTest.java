package scope.dev.bank.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import scope.dev.bank.TestcontainersConfiguration;
import scope.dev.bank.account.Account;
import scope.dev.bank.account.AccountNotFoundException;
import scope.dev.bank.account.InsufficientFundsException;
import scope.dev.bank.common.Money;
import scope.dev.bank.support.TestData;
import scope.dev.bank.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class TransferServiceTest {

    @Autowired TransferService transferService;
    @Autowired MoneyTransactionRepository transactions;
    @Autowired TestData data;

    @Test
    void movesMoneyAndRecordsTransaction() {
        User alice = data.newUser();
        User bob = data.newUser();
        Account from = data.newAccount(alice, "100.00");
        Account to = data.newAccount(bob, "5.00");

        transferService.transfer(alice.getId(), from.getAccountNumber(), to.getAccountNumber(), Money.of("40.00"));

        assertThat(data.balanceOf(from)).isEqualTo(Money.of("60.00"));
        assertThat(data.balanceOf(to)).isEqualTo(Money.of("45.00"));
        assertThat(transactions.findBySourceAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                from.getId(), from.getId(), Pageable.unpaged())).hasSize(2);

    }

    @Test
    void insufficientFundsChangesNothing() {
        User alice = data.newUser();
        User bob = data.newUser();
        Account from = data.newAccount(alice, "10.00");
        Account to = data.newAccount(bob, "0.00");

        assertThatThrownBy(() -> transferService.transfer(alice.getId(), from.getAccountNumber(), to.getAccountNumber(), Money.of("10.01")))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(data.balanceOf(from)).isEqualTo(Money.of("10.00"));
        assertThat(data.balanceOf(to)).isEqualTo(Money.ZERO);
    }

    @Test
    void unknownTargetChangesNothing() {
        User alice = data.newUser();
        Account from = data.newAccount(alice, "10.00");

        assertThatThrownBy(() -> transferService.transfer(alice.getId(), from.getAccountNumber(), "999999999", Money.of("1.00")))
                .isInstanceOf(AccountNotFoundException.class);

        assertThat(data.balanceOf(from)).isEqualTo(Money.of("10.00"));
    }

    @Test
    void cannotSpendFromSomeoneElsesAccount() {
        User alice = data.newUser();
        User mallory = data.newUser();
        Account aliceAccount = data.newAccount(alice, "100.00");
        Account malloryAccount = data.newAccount(mallory, "0.00");

        assertThatThrownBy(() -> transferService.transfer(
                mallory.getId(), aliceAccount.getAccountNumber(), malloryAccount.getAccountNumber(),  Money.of("50.00")
        )).isInstanceOf(AccountNotFoundException.class);

        assertThat(data.balanceOf(aliceAccount)).isEqualTo(Money.of("100.00"));
    }

    @Test
    void rejectsTransferToSameAccount() {
        User alice = data.newUser();
        Account account = data.newAccount(alice, "10.00");

        assertThatThrownBy(() -> transferService.transfer(
                alice.getId(), account.getAccountNumber(), account.getAccountNumber(), Money.of("1.00")
        )).isInstanceOf(InvalidTransferException.class);
    }
}
