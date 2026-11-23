package scope.dev.bank.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(UUID id, TransactionType type, BigDecimal amount, Instant createdAt) {
    public static TransactionResponse from (MoneyTransaction tx) {
        return new TransactionResponse(tx.getId(), tx.getType(), tx.getAmount().amount(), tx.getCreatedAt());
    }
}
