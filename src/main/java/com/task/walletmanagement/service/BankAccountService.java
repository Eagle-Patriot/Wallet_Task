package com.task.walletmanagement.service;

import com.task.walletmanagement.dto.BankAccountResponse;
import com.task.walletmanagement.dto.LinkBankAccountRequest;
import com.task.walletmanagement.entity.BankAccount;
import com.task.walletmanagement.entity.Wallet;
import com.task.walletmanagement.exception.BankAccountNotFoundException;
import com.task.walletmanagement.exception.DuplicateBankAccountException;
import com.task.walletmanagement.repository.BankAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for bank account operations.
 */
@Service
public class BankAccountService {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);
    private final BankAccountRepository bankAccountRepository;
    private final WalletService walletService;

    public BankAccountService(BankAccountRepository bankAccountRepository, WalletService walletService) {
        this.bankAccountRepository = bankAccountRepository;
        this.walletService = walletService;
    }

    /**
     * Link a bank account to a wallet.
     */
    @Transactional
    public BankAccountResponse linkBankAccount(Long walletId, LinkBankAccountRequest request) {
        logger.info("Linking bank account {} to wallet ID: {}", request.getAccountNumber(), walletId);

        // Validate wallet exists
        Wallet wallet = walletService.getWalletById(walletId);

        // Check if bank account already exists
        if (bankAccountRepository.findByAccountNumberAndBank(request.getAccountNumber(), request.getBank())
                .isPresent()) {
            throw new DuplicateBankAccountException(request.getAccountNumber(), request.getBank());
        }

        BankAccount bankAccount = new BankAccount();
        bankAccount.setWallet(wallet);
        bankAccount.setAccountNumber(request.getAccountNumber());
        bankAccount.setAccountName(request.getAccountName());
        bankAccount.setBank(request.getBank());

        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        logger.info("Bank account linked successfully with ID: {}", savedAccount.getId());

        return mapToResponse(savedAccount);
    }

    /**
     * Get all bank accounts linked to a wallet.
     */
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getBankAccountsByWalletId(Long walletId) {
        logger.info("Fetching bank accounts for wallet ID: {}", walletId);

        // Validate wallet exists
        walletService.getWalletById(walletId);

        List<BankAccount> bankAccounts = bankAccountRepository.findByWalletId(walletId);

        return bankAccounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get bank account by account number, bank, and wallet ID (internal use).
     */
    @Transactional(readOnly = true)
    public BankAccount getBankAccountByAccountNumberAndWallet(String accountNumber, String bank, Long walletId) {
        return bankAccountRepository.findByAccountNumberAndBankAndWalletId(accountNumber, bank, walletId)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found with account number " + accountNumber +
                                " and bank " + bank + " linked to wallet ID " + walletId));
    }

    /**
     * Map bank account entity to response DTO.
     */
    private BankAccountResponse mapToResponse(BankAccount bankAccount) {
        return new BankAccountResponse(
                bankAccount.getId(),
                bankAccount.getAccountNumber(),
                bankAccount.getAccountName(),
                bankAccount.getBank(),
                bankAccount.getCreatedAt());
    }
}
