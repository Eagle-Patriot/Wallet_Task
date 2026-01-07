package com.task.walletmanagement.service.payment;

import com.task.walletmanagement.exception.PaymentProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Paystack payment gateway implementation (simulated).
 */
@Component
public class PaystackPaymentStrategy implements PaymentGatewayStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PaystackPaymentStrategy.class);
    private final Random random = new Random();

    @Override
    public void processPayment(String accountNumber, BigDecimal amount) {
        logger.info("===========================================");
        logger.info("PAYSTACK PAYMENT GATEWAY");
        logger.info("===========================================");
        logger.info("Processing payment via PAYSTACK");
        logger.info("Account Number: {}", accountNumber);
        logger.info("Amount: {}", amount);
        logger.info("===========================================");

        // Simulate payment processing delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate random failure (10% chance) for testing rollback
        if (random.nextInt(10) == 0) {
            logger.error("PAYSTACK: Payment processing FAILED for account {}", accountNumber);
            throw new PaymentProcessingException("Paystack payment failed: Transaction declined by bank");
        }

        logger.info("PAYSTACK: Payment processed SUCCESSFULLY");
        logger.info("===========================================");
    }
}
