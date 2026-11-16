package scope.dev.bank.user;

import scope.dev.bank.common.DomainException;

public class DuplicateEmailException extends DomainException {
    public DuplicateEmailException() {
        super("Email is already registered.");
    }
}
