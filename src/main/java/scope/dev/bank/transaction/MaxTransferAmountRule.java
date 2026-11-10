package scope.dev.bank.transaction;


import org.springframework.stereotype.Component;
import scope.dev.bank.account.Account;
import scope.dev.bank.common.Money;

@Component
public class MaxTransferAmountRule implements TransferRule{

    private final Money max;

    MaxTransferAmountRule(TransferProperties properties) {
        this.max = Money.of(properties.maxAmount());
    }
    @Override
    public void check(Account source, Account target, Money amount) {
        if (amount.isGreaterThan(max)) {
            throw new InvalidTransferException("Transfer amount exceeds the limit of " + max);
        }
    }
}
