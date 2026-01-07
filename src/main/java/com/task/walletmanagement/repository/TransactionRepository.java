package com.task.walletmanagement.repository;

import com.task.walletmanagement.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a specific wallet, ordered by creation time (newest
     * first).
     */
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}
