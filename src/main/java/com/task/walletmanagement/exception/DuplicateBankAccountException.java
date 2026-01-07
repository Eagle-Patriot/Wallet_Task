package com.task.walletmanagement.exception;

/**
 * Exception thrown when attempting to link a bank account that already exists.
 */
public class DuplicateBankAccountException extends RuntimeException {
    public DuplicateBankAccountException(String accountNumber, String bank) {
        super(String.format("Bank account already exists with account number %s and bank %s", accountNumber, bank));
    }
}
