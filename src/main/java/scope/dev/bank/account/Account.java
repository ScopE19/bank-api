package scope.dev.bank.account;

import jakarta.persistence.*;
import scope.dev.bank.common.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
@Entity
@Table(name = "account")
public class Account {
    @Id
    private UUID id;
    @Column(name = "owner_id", nullable = false, updatable = false)
    private UUID ownerId;
    @Column(name = "account_number", nullable = false, updatable = false, unique = true)
    private String accountNumber;
    @Column(nullable = false)
    private Money balance;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Account() {

    }

    public Account(UUID ownerId, String accountNumber) {
        this.id = UUID.randomUUID();
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
        this.accountNumber = Objects.requireNonNull(accountNumber, "accountNumber");
        this.balance = Money.ZERO;
        this.createdAt = Instant.now();
    }

    public void deposit(Money amount) {
        requirePositive(amount);
        balance = balance.plus(amount);
    }

    public void withdraw(Money amount) {
        requirePositive(amount);
        if(balance.isLessThan(amount)) {
            throw new InsufficientFundsException(accountNumber);
        }
        balance = balance.minus(amount);
    }

    public boolean isOwnedBy(UUID userId) {
        return ownerId.equals(userId);
    }

    private static void requirePositive(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getVersion() { return version; }
}
