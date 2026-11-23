package scope.dev.bank.transaction;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import scope.dev.bank.auth.CurrentUser;
import scope.dev.bank.common.Money;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody TransferRequest request) {
        return TransactionResponse.from(transferService.transfer(CurrentUser.id(jwt), request.fromAccountNumber(), request.toAccountNumber(), Money.of(request.amount())));
    }
}
