package scope.dev.bank.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scope.dev.bank.account.Account;
import scope.dev.bank.account.AccountNotFoundException;
import scope.dev.bank.account.AccountRepository;
import scope.dev.bank.common.Money;

import java.util.*;

@Service
public class TransferService {

    private final AccountRepository accounts;
    private final MoneyTransactionRepository transactions;

    private final List<TransferRule> rules;

    public TransferService(AccountRepository accounts, MoneyTransactionRepository transactions, List<TransferRule> rules) {
        this.accounts = accounts;
        this.transactions = transactions;
        this.rules = rules;
    }

    @Transactional
    public MoneyTransaction transfer(UUID userId, String fromNumber, String toNumber, Money amount) {

        Map<String, Account> locked = lockInOrder(fromNumber, toNumber);
        Account source = locked.get(fromNumber);
        Account target = locked.get(toNumber);

        if (!source.isOwnedBy(userId)) {
            throw new AccountNotFoundException(fromNumber);
        }
        rules.forEach(rule -> rule.check(source, target, amount));

        source.withdraw(amount);
        target.deposit(amount);

        return transactions.save(MoneyTransaction.transfer(source.getId(), target.getId(), amount, userId));
    }

    private Map<String, Account> lockInOrder(String... accountNumbers) {
        Map<String, Account> result = new HashMap<>();
        for (String number : new TreeSet<>(Arrays.asList(accountNumbers))) {
            result.put(number, accounts.findByAccountNumberForUpdate(number)
                    .orElseThrow(() -> new AccountNotFoundException(number)));
        }
        return result;
    }
}
