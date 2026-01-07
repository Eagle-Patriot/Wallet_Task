package com.task.walletmanagement.repository;

import com.task.walletmanagement.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Wallet entity operations.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Find wallet by email address.
     */
    Optional<Wallet> findByEmail(String email);

    /**
     * Find wallet by ID with pessimistic write lock for concurrent payment
     * operations.
     * This ensures that only one transaction can modify the wallet balance at a
     * time.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdWithLock(@Param("id") Long id);

    /**
     * Check if a wallet exists with the given email.
     */
    boolean existsByEmail(String email);
}
