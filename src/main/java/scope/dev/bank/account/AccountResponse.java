package scope.dev.bank.account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(String accountNumber, BigDecimal balance, Instant createdAt) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getAccountNumber(), account.getBalance().amount(), account.getCreatedAt());
    }
}
