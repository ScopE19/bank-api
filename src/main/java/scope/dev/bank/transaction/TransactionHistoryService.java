package scope.dev.bank.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scope.dev.bank.account.Account;
import scope.dev.bank.account.AccountService;

import java.util.UUID;

@Service
public class TransactionHistoryService {
    private final AccountService accountService;
    private final MoneyTransactionRepository transactions;

    public TransactionHistoryService(AccountService accountService, MoneyTransactionRepository transactions) {
        this.accountService = accountService;
        this.transactions = transactions;
    }

    @Transactional(readOnly = true)
    public Page<StatementLine> history(UUID userId, String accountNumber, int page, int size) {
        Account account = accountService.getOwnedAccount(userId, accountNumber);
        return transactions
                .findBySourceAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                        account.getId(), account.getId(), PageRequest.of(page, size))
                .map(tx -> new StatementLine(tx, account.getId().equals(tx.getTargetAccountId()) ? Direction.IN : Direction.OUT));

    }
}
