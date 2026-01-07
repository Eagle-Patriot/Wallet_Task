package com.task.walletmanagement.exception;

/**
 * Exception thrown when a bank account is not found.
 */
public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String message) {
        super(message);
    }

    public BankAccountNotFoundException(String accountNumber, String bank) {
        super(String.format("Bank account not found with account number %s and bank %s", accountNumber, bank));
    }
}
