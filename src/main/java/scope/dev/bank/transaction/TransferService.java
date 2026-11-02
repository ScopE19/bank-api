package scope.dev.bank.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scope.dev.bank.account.Account;
import scope.dev.bank.account.AccountNotFoundException;
import scope.dev.bank.account.AccountRepository;
import scope.dev.bank.common.Money;

import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accounts;
    private final MoneyTransactionRepository transactions;

    public TransferService(AccountRepository accounts, MoneyTransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Transactional
    public MoneyTransaction transfer(UUID userId, String fromNumber, String toNumber, Money amount) {
        if (fromNumber.equals(toNumber)) {
            throw new InvalidTransferException("Cannot transfer to the same account");

        }
        Account source = accounts.findByAccountNumber(fromNumber)
                .filter(account -> account.isOwnedBy(userId))
                .orElseThrow(() -> new AccountNotFoundException(fromNumber));
        Account target = accounts.findByAccountNumber(toNumber)
                .orElseThrow(() -> new AccountNotFoundException(toNumber));

        source.withdraw(amount);
        target.deposit(amount);

        return transactions.save(MoneyTransaction.transfer(source.getId(), target.getId(), amount, userId));
    }
}
