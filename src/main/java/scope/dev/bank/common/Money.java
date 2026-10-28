package scope.dev.bank.common;

import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) implements Comparable<Money> {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Money supports at most 2 decimal places: " + amount);
        }
        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static Money of(String value){
        return new Money(new BigDecimal(value));
    }

    public static Money of(BigDecimal value){
        return new Money(value);
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money minus(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public boolean isLessThan(Money other) {
        return  compareTo(other) < 0;
    }

    public boolean isGreaterThan(Money other) {
        return  compareTo(other) > 0;
    }

    @Override
    public int compareTo(@NonNull Money other) {
        return amount.compareTo(other.amount);
    }

    @Override
    public @NonNull String toString() {
        return amount.toPlainString();
    }


}
