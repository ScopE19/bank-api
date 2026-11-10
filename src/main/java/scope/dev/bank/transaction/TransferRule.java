package scope.dev.bank.transaction;

import scope.dev.bank.account.Account;
import scope.dev.bank.common.Money;

public interface TransferRule {
    void check(Account source, Account target, Money amount);
}
