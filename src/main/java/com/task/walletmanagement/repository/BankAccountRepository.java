package com.task.walletmanagement.repository;

import com.task.walletmanagement.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BankAccount entity operations.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    /**
     * Find all bank accounts linked to a specific wallet.
     */
    List<BankAccount> findByWalletId(Long walletId);

    /**
     * Find a bank account by account number and bank.
     */
    Optional<BankAccount> findByAccountNumberAndBank(String accountNumber, String bank);

    /**
     * Find a bank account by account number, bank, and wallet ID.
     */
    Optional<BankAccount> findByAccountNumberAndBankAndWalletId(String accountNumber, String bank, Long walletId);
}
