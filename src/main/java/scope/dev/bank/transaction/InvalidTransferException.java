package scope.dev.bank.transaction;

import scope.dev.bank.common.DomainException;

public class InvalidTransferException extends DomainException {
    public InvalidTransferException(String message) {
        super(message);
    }
}
