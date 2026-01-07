package com.task.walletmanagement.service;

import com.task.walletmanagement.dto.TransactionResponse;
import com.task.walletmanagement.entity.Transaction;
import com.task.walletmanagement.entity.Wallet;
import com.task.walletmanagement.enums.PaymentGateway;
import com.task.walletmanagement.enums.TransactionStatus;
import com.task.walletmanagement.enums.TransactionType;
import com.task.walletmanagement.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for transaction operations.
 */
@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionService(TransactionRepository transactionRepository, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    /**
     * Get all transactions for a wallet.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByWalletId(Long walletId) {
        logger.info("Fetching transactions for wallet ID: {}", walletId);

        // Validate wallet exists
        walletService.getWalletById(walletId);

        List<Transaction> transactions = transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId);

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new transaction.
     */
    @Transactional
    public Transaction createTransaction(Wallet wallet, BigDecimal amount, TransactionType type,
            String description, PaymentGateway paymentGateway,
            TransactionStatus status) {
        logger.info("Creating transaction for wallet ID: {}, amount: {}, type: {}, status: {}",
                wallet.getId(), amount, type, status);

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setPaymentGateway(paymentGateway);
        transaction.setStatus(status);

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction created with ID: {}", savedTransaction.getId());

        return savedTransaction;
    }

    /**
     * Map transaction entity to response DTO.
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getPaymentGateway(),
                transaction.getStatus(),
                transaction.getCreatedAt());
    }
}
