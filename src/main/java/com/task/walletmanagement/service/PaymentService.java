package com.task.walletmanagement.service;

import com.task.walletmanagement.dto.FundWalletRequest;
import com.task.walletmanagement.dto.TransactionResponse;
import com.task.walletmanagement.entity.BankAccount;
import com.task.walletmanagement.entity.Transaction;
import com.task.walletmanagement.entity.Wallet;
import com.task.walletmanagement.enums.TransactionStatus;
import com.task.walletmanagement.enums.TransactionType;
import com.task.walletmanagement.exception.PaymentProcessingException;
import com.task.walletmanagement.repository.WalletRepository;
import com.task.walletmanagement.service.payment.PaymentGatewayFactory;
import com.task.walletmanagement.service.payment.PaymentGatewayStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for payment operations.
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final WalletRepository walletRepository;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final PaymentGatewayFactory paymentGatewayFactory;

    public PaymentService(WalletRepository walletRepository,
            BankAccountService bankAccountService,
            TransactionService transactionService,
            PaymentGatewayFactory paymentGatewayFactory) {
        this.walletRepository = walletRepository;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
        this.paymentGatewayFactory = paymentGatewayFactory;
    }

    /**
     * Fund a wallet from a linked bank account via payment gateway.
     * all steps succeed or all are rolled back.
     * 
     * Steps:
     * 1. Lock wallet for update
     * 2. Validate bank account is linked to wallet
     * 3. Process payment via payment gateway
     * 4. Update wallet balance
     * 5. Record transaction
     * 
     * If any step fails, the entire transaction is rolled back.
     */
    @Transactional
    public TransactionResponse fundWallet(Long walletId, FundWalletRequest request) {
        logger.info("Processing wallet funding for wallet ID: {} via {}", walletId, request.getPaymentGateway());

        try {
            // Step 1: Get wallet to prevent concurrent modifications
            Wallet wallet = walletRepository.findByIdWithLock(walletId)
                    .orElseThrow(() -> new PaymentProcessingException("Wallet not found with ID: " + walletId));

            logger.info("Wallet locked for update. Current balance: {}", wallet.getBalance());

            // Step 2: Validate bank account is linked to this wallet
            // Find the bank account linked to this wallet with the given account number
            BankAccount bankAccount = bankAccountService.getBankAccountsByWalletId(walletId).stream()
                    .map(response -> {
                        try {
                            return bankAccountService.getBankAccountByAccountNumberAndWallet(
                                    response.getAccountNumber(),
                                    response.getBank(),
                                    walletId);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(ba -> ba != null && ba.getAccountNumber().equals(request.getAccountNumber()))
                    .findFirst()
                    .orElseThrow(() -> new PaymentProcessingException(
                            "Bank account with number " + request.getAccountNumber() +
                                    " is not linked to this wallet"));
            logger.info("Bank account validated: {} - {}", bankAccount.getBank(), bankAccount.getAccountNumber());

            // Step 3: Process payment via payment gateway
            PaymentGatewayStrategy paymentGateway = paymentGatewayFactory.getStrategy(request.getPaymentGateway());
            paymentGateway.processPayment(request.getAccountNumber(), request.getAmount());

            // Step 4: Update wallet balance
            BigDecimal previousBalance = wallet.getBalance();
            BigDecimal newBalance = previousBalance.add(request.getAmount());
            wallet.setBalance(newBalance);
            walletRepository.save(wallet);

            logger.info("Wallet balance updated from {} to {}", previousBalance, newBalance);

            // Step 5: Record successful transaction
            Transaction transaction = transactionService.createTransaction(
                    wallet,
                    request.getAmount(),
                    TransactionType.CREDIT,
                    String.format("Wallet funded via %s from account %s",
                            request.getPaymentGateway(), request.getAccountNumber()),
                    request.getPaymentGateway(),
                    TransactionStatus.SUCCESS);

            logger.info("Payment processing completed successfully");

            return mapToResponse(transaction);

        } catch (PaymentProcessingException e) {
            // Payment gateway failed - transaction will be rolled back automatically
            logger.error("Payment processing failed, transaction will be rolled back: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Any other error - transaction will be rolled back automatically
            logger.error("Unexpected error during payment processing, transaction will be rolled back", e);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage(), e);
        }
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
