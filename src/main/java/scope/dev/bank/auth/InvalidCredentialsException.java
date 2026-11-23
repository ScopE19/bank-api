package scope.dev.bank.auth;

import scope.dev.bank.common.DomainException;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
