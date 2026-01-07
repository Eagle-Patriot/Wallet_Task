package com.task.walletmanagement.service.payment;

import java.math.BigDecimal;

/**
 * Strategy interface for payment gateway integrations.
 * New payment gateways can be added by implementing this interface.
 */
public interface PaymentGatewayStrategy {

    /**
     * Process a payment via the payment gateway.
     * 
     * @param accountNumber Bank account number
     * @param amount        Amount to charge
     * @throws com.task.walletmanagement.exception.PaymentProcessingException
     * 
     * 
     */
    void processPayment(String accountNumber, BigDecimal amount);
}
