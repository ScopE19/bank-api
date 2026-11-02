package scope.dev.bank.transaction;

import jakarta.persistence.*;
import scope.dev.bank.account.Account;
import scope.dev.bank.common.Money;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "money_transaction")
public class MoneyTransaction {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionType type;

    @Column(name = "source_account_id", updatable = false)
    private UUID sourceAccountId;

    @Column(name = "target_account_id", updatable = false)
    private UUID targetAccountId;

    @Column(nullable = false, updatable = false)
    private Money amount;

    @Column(name = "initiated_by", nullable = false, updatable = false)
    private UUID initiatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MoneyTransaction() {
    }

    private MoneyTransaction(TransactionType type, UUID sourceAccountId, UUID targetAccountId,
                             Money amount, UUID initiatedBy) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.initiatedBy = initiatedBy;
        this.createdAt = Instant.now();
    }

    public static MoneyTransaction deposit(UUID accountId, Money amount, UUID initiatedBy) {
        return new MoneyTransaction(TransactionType.DEPOSIT, null, accountId, amount, initiatedBy);
    }

    public static MoneyTransaction withdrawal(UUID accountId, Money amount, UUID initiatedBy) {
        return new MoneyTransaction(TransactionType.WITHDRAWAL, accountId, null, amount, initiatedBy);
    }

    public static MoneyTransaction transfer(UUID sourceId, UUID targetId, Money amount, UUID initiatedBy) {
        return new MoneyTransaction(TransactionType.TRANSFER, sourceId, targetId, amount, initiatedBy);
    }

    public UUID getId() { return id; }
    public TransactionType getType() { return type; }
    public UUID getSourceAccountId() { return sourceAccountId; }
    public UUID getTargetAccountId() { return targetAccountId; }
    public Money getAmount() { return amount; }
    public UUID getInitiatedBy() { return initiatedBy; }
    public Instant getCreatedAt() { return createdAt; }


}
