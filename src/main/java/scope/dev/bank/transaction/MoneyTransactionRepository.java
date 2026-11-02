package scope.dev.bank.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MoneyTransactionRepository extends JpaRepository<MoneyTransaction, UUID> {
    Page<MoneyTransaction> findBySourceAccountIdOrTargetAccountIdOrderByCreatedAtDesc(UUID sourceAccountId, UUID targetAccountId, Pageable pageable);
}
