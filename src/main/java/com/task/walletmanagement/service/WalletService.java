package com.task.walletmanagement.service;

import com.task.walletmanagement.dto.CreateWalletRequest;
import com.task.walletmanagement.dto.WalletResponse;
import com.task.walletmanagement.entity.Wallet;
import com.task.walletmanagement.exception.DuplicateEmailException;
import com.task.walletmanagement.exception.WalletNotFoundException;
import com.task.walletmanagement.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for wallet operations.
 */
@Service
public class WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Create a new wallet.
     */
    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        logger.info("Creating wallet for email: {}", request.getEmail());

        // Check if wallet already exists with this email
        if (walletRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Wallet wallet = new Wallet();
        wallet.setEmail(request.getEmail());
        wallet.setPhoneNumber(request.getPhoneNumber());
        wallet.setBalance(BigDecimal.ZERO);

        Wallet savedWallet = walletRepository.save(wallet);
        logger.info("Wallet created successfully with ID: {}", savedWallet.getId());

        return mapToResponse(savedWallet);
    }

    /**
     * Get wallet by email.
     */
    @Transactional(readOnly = true)
    public WalletResponse getWalletByEmail(String email) {
        logger.info("Fetching wallet for email: {}", email);

        Wallet wallet = walletRepository.findByEmail(email)
                .orElseThrow(() -> new WalletNotFoundException("email", email));

        return mapToResponse(wallet);
    }

    /**
     * Get wallet by ID.
     */
    @Transactional(readOnly = true)
    public Wallet getWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    /**
     * Map wallet entity to response DTO.
     */
    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getEmail(),
                wallet.getPhoneNumber(),
                wallet.getBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt());
    }
}
