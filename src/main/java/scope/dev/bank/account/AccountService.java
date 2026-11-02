package scope.dev.bank.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private static final int MAX_NUMBER_ATTEMPTS = 5;

    private final AccountRepository accounts;
    private final AccountNumberGenerator numberGenerator;

    public AccountService(AccountRepository accounts, AccountNumberGenerator numberGenerator) {
        this.accounts = accounts;
        this.numberGenerator = numberGenerator;
    }

    @Transactional
    public Account openAccount(UUID ownerId) {
        for (int attempt = 0; attempt < MAX_NUMBER_ATTEMPTS; attempt++) {
            String number = numberGenerator.next();
            if (!accounts.existsByAccountNumber(number)) {
                return accounts.save(new Account(ownerId, number));
            }
        }
        throw new IllegalStateException("Could not generate a unique account number");
    }

    @Transactional(readOnly = true)
    public List<Account> listAccounts(UUID ownerId) {
        return accounts.findAllByOwnerIdOrderByCreatedAtAsc(ownerId);
    }

    @Transactional(readOnly = true)
    public Account getOwnedAccount(UUID ownerId, String accountNumber) {
        return accounts.findByAccountNumber(accountNumber)
                .filter(account -> account.isOwnedBy(ownerId))
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
