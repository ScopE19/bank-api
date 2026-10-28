package scope.dev.bank.common;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MoneyTest {

    @Test
    void normalizesToTwoDecimalPlaces() {
        assertThat(Money.of("10")).isEqualTo(Money.of("10.00"));
        assertThat(Money.of("10").amount().scale()).isEqualTo(2);
    }

    @Test
    void rejectsMoreThanTwoDecimalPlaces() {
        assertThatThrownBy(() -> Money.of("10.001")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> Money.of((BigDecimal) null)).isInstanceOf(NullPointerException.class);

    }

    @Test
    void addsWithoutFloatingPointErrors() {
        assertThat(Money.of("0.10").plus(Money.of("0.20"))).isEqualTo(Money.of("0.30"));
    }

    @Test
    void subtractsAndCanGoNegativeBecauseRulesLiveInAccount() {
        assertThat(Money.of("5.00").minus(Money.of("7.50"))).isEqualTo(Money.of("-2.50"));
    }

    @Test
    void comparesByValue() {
        assertThat(Money.of("5.00").isLessThan(Money.of("5.01"))).isTrue();
        assertThat(Money.of("5.00").isGreaterThan(Money.of("4.99"))).isTrue();
        assertThat(Money.of("0.00").isPositive()).isFalse();
        assertThat(Money.of("0.01").isPositive()).isTrue();
    }

    @Test
    void lowScaleNumbersScalesTo2() {
        assertThat(Money.of("10.5").equals(Money.of("10.50"))).isTrue();
    }
}
