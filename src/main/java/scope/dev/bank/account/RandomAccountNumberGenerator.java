package scope.dev.bank.account;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
@Component
public class RandomAccountNumberGenerator implements AccountNumberGenerator {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String next() {
        return String.format("%012d", random.nextLong(1_000_000_000_000L));
    }
}
