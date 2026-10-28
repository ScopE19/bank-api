package scope.dev.bank.account;

import scope.dev.bank.common.DomainException;

public class InvalidAmountException extends DomainException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
