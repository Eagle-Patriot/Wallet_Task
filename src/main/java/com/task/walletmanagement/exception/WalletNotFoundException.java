package com.task.walletmanagement.exception;

/**
 * Exception thrown when a wallet is not found.
 */
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }

    public WalletNotFoundException(Long walletId) {
        super(String.format("Wallet not found with ID: %d", walletId));
    }

    public WalletNotFoundException(String field, String value) {
        super(String.format("Wallet not found with %s: %s", field, value));
    }
}
