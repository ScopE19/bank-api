package scope.dev.bank.support;

import org.springframework.stereotype.Component;
import scope.dev.bank.account.Account;
import scope.dev.bank.account.AccountRepository;
import scope.dev.bank.account.AccountService;
import scope.dev.bank.common.Money;
import scope.dev.bank.transaction.CashService;
import scope.dev.bank.user.User;
import scope.dev.bank.user.UserRepository;

import java.util.UUID;

@Component
public class TestData {

    private final UserRepository users;
    private final AccountService accountService;
    private final AccountRepository accounts;
    private final CashService cashService;

    public TestData(UserRepository users, AccountService accountService, AccountRepository accounts, CashService cashService) {
        this.users = users;
        this.accountService = accountService;
        this.accounts = accounts;
        this.cashService = cashService;
    }

    public User newUser() {
        return users.save(new User("u-" + UUID.randomUUID() + "@example.com", "hash", "Test User"));
    }

    public Account newAccount(User owner, String initialBalance) {
        Account account = accountService.openAccount(owner.getId());
        Money money = Money.of(initialBalance);
        if (money.isPositive()) {
            cashService.deposit(owner.getId(), account.getAccountNumber(), money);
        }
        return accounts.findById(account.getId()).orElseThrow();
    }

    public Money balanceOf(Account account) {
        return accounts.findById(account.getId()).orElseThrow().getBalance();
    }


}
