package scope.dev.bank.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record TransferRequest(@NotNull @Pattern(regexp = "\\d{12}") String fromAccountNumber,
                              @NotNull @Pattern(regexp = "\\d{12}") String toAccountNumber,
                              @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount) {
}
