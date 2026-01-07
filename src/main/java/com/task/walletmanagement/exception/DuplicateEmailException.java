package com.task.walletmanagement.exception;

/**
 * Exception thrown when attempting to create a wallet with a duplicate email.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(String.format("Wallet already exists with email: %s", email));
    }
}
