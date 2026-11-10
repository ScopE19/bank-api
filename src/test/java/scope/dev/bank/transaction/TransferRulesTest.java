package scope.dev.bank.transaction;

import org.junit.jupiter.api.Test;
import scope.dev.bank.account.Account;
import scope.dev.bank.common.Money;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TransferRulesTest {
    private final Account a = new Account(UUID.randomUUID(), "000000001");
    private final Account b = new Account(UUID.randomUUID(), "000000002");

    @Test
    void rejectsSameAccount() {
        assertThatThrownBy(() -> new NotSameAccountRule().check(a, a, Money.of("1.00")))
                .isInstanceOf(InvalidTransferException.class);
        assertThatCode(() -> new NotSameAccountRule().check(a, b, Money.of("1.00")))
                .doesNotThrowAnyException();
    }

    @Test
    void enforcesMaximumAmount() {
        MaxTransferAmountRule rule = new MaxTransferAmountRule(new TransferProperties(new BigDecimal("100.00")));

        assertThatCode(() -> rule.check(a, b, Money.of("100.00"))).doesNotThrowAnyException();
        assertThatThrownBy(() -> rule.check(a, b, Money.of("100.01")))
                .isInstanceOf(InvalidTransferException.class);
    }
}
