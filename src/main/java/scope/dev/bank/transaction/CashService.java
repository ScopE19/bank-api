package scope.dev.bank.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scope.dev.bank.account.Account;
import scope.dev.bank.account.AccountNotFoundException;
import scope.dev.bank.account.AccountRepository;
import scope.dev.bank.common.Money;

import java.util.UUID;

@Service
public class CashService {

    private final AccountRepository accounts;
    private final MoneyTransactionRepository transactions;

    public CashService(AccountRepository accounts, MoneyTransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Transactional
    public MoneyTransaction deposit(UUID userId, String accountNumber, Money amount) {
        Account account = loadOwned(userId, accountNumber);
        account.deposit(amount);
        return transactions.save(MoneyTransaction.deposit(account.getId(), amount, userId));
    }

    @Transactional
    public MoneyTransaction withdraw(UUID userId, String accountNumber, Money amount) {
        Account account = loadOwned(userId, accountNumber);
        account.withdraw(amount);
        return transactions.save(MoneyTransaction.withdrawal(account.getId(), amount, userId));
    }

    private Account loadOwned(UUID userId, String accountNumber) {
        return accounts.findByAccountNumber(accountNumber)
                .filter(account -> account.isOwnedBy(userId))
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
