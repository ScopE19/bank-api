package scope.dev.bank.account;

import scope.dev.bank.common.DomainException;

public class InsufficientFundsException extends DomainException {
    public InsufficientFundsException(String accountNumber) {

        super("Insufficient funds in account " + accountNumber);
    }
}
