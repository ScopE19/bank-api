package scope.dev.bank.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scope.dev.bank.common.Money;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountTest {

    private final UUID ownerId = UUID.randomUUID();
    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account(ownerId, "00000001");
    }

    @Test
    void startsWithZeroBalance() {
        assertThat(account.getBalance()).isEqualTo(Money.ZERO);
    }

    @Test
    void depositIncreasesBalance() {
        account.deposit(Money.of("100.50"));
        assertThat(account.getBalance()).isEqualTo(Money.of("100.50"));
    }

    @Test
    void withdrawDecreasesBalance() {
        account.deposit(Money.of("100.00"));
        account.withdraw(Money.of("30.25"));
        assertThat(account.getBalance()).isEqualTo(Money.of("69.75"));
    }

    @Test
    void withdrawingMoreThanBalanceFailsAndLeavesBalanceUntouched() {
        account.deposit(Money.of("50.00"));
        assertThatThrownBy(() -> account.withdraw(Money.of("50.01"))).isInstanceOf(InsufficientFundsException.class);
        assertThat(account.getBalance()).isEqualTo(Money.of("50.00"));

    }

    @Test
    void canWithdrawEntireBalance() {
        account.deposit(Money.of("50.00"));
        account.withdraw(Money.of("50.00"));
        assertThat(account.getBalance()).isEqualTo(Money.ZERO);
    }

    @Test
    void rejectsZeroAndNegativeAmounts() {
        assertThatThrownBy(() -> account.deposit(Money.ZERO)).isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.deposit(Money.of("-1.00"))).isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.withdraw(Money.of("-1.00"))).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void knowsItsOwner() {
        assertThat(account.isOwnedBy(ownerId)).isTrue();
        assertThat(account.isOwnedBy(UUID.randomUUID())).isFalse();
    }
}
