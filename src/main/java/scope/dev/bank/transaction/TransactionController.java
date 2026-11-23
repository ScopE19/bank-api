package scope.dev.bank.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scope.dev.bank.auth.CurrentUser;
import scope.dev.bank.common.Money;
import scope.dev.bank.common.PageResponse;

@RestController
@Validated
@RequestMapping("/api/v1/accounts/{accountNumber}")
public class TransactionController {
    private static final String ACCOUNT_NUMBER_REGEX = "^[0-9]{12}$";
    private static final String ACCOUNT_NUMBER_MESSAGE = "Account number must be a 12-digit number";

    public record StatementLineResponse(TransactionResponse transaction, Direction direction) {}

    private final CashService cashService;
    private final TransactionHistoryService historyService;

    public TransactionController(CashService cashService, TransactionHistoryService historyService) {
        this.cashService = cashService;
        this.historyService = historyService;
    }

    @PostMapping("/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @Pattern(regexp = ACCOUNT_NUMBER_REGEX, message = ACCOUNT_NUMBER_MESSAGE) String accountNumber,
            @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.from(
                cashService.deposit(CurrentUser.id(jwt), accountNumber, Money.of(request.amount())));
    }

    @PostMapping("/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @Pattern(regexp = ACCOUNT_NUMBER_REGEX, message = ACCOUNT_NUMBER_MESSAGE) String accountNumber,
            @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.from(
                cashService.withdraw(CurrentUser.id(jwt), accountNumber, Money.of(request.amount())));
    }

    @GetMapping("/transactions")
    public PageResponse<StatementLineResponse> history(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable @Pattern(regexp = ACCOUNT_NUMBER_REGEX, message = ACCOUNT_NUMBER_MESSAGE) String accountNumber,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(historyService.history(CurrentUser.id(jwt), accountNumber, page, size)
                .map(line -> new StatementLineResponse(TransactionResponse.from(line.transaction()), line.direction())));
    }
}
