package scope.dev.bank.account;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import scope.dev.bank.auth.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse open(@AuthenticationPrincipal Jwt jwt) {
        return AccountResponse.from(accountService.openAccount(CurrentUser.id(jwt)));
    }

    @GetMapping
    public List<AccountResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return accountService.listAccounts(CurrentUser.id(jwt)).stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable String accountNumber) {
        return AccountResponse.from(accountService.getOwnedAccount(CurrentUser.id(jwt), accountNumber));
    }
}
