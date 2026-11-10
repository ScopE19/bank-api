package scope.dev.bank.transaction;

import org.springframework.stereotype.Component;
import scope.dev.bank.account.Account;
import scope.dev.bank.common.Money;

@Component
public class NotSameAccountRule implements TransferRule{
    @Override
    public void check(Account source, Account target, Money amount) {
        if(source.getId().equals(target.getId())) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }
    }
}
