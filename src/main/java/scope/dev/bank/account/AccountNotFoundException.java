package scope.dev.bank.account;

import scope.dev.bank.common.DomainException;

public class AccountNotFoundException extends DomainException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
