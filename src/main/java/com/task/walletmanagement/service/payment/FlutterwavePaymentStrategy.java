package com.task.walletmanagement.service.payment;

import com.task.walletmanagement.exception.PaymentProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Flutterwave payment gateway implementation (simulated).
 */
@Component
public class FlutterwavePaymentStrategy implements PaymentGatewayStrategy {

    private static final Logger logger = LoggerFactory.getLogger(FlutterwavePaymentStrategy.class);
    private final Random random = new Random();

    @Override
    public void processPayment(String accountNumber, BigDecimal amount) {
        logger.info("===========================================");
        logger.info("FLUTTERWAVE PAYMENT GATEWAY");
        logger.info("===========================================");
        logger.info("Processing payment via FLUTTERWAVE");
        logger.info("Account Number: {}", accountNumber);
        logger.info("Amount: {}", amount);
        logger.info("===========================================");

        // Simulate payment processing delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate random failure for testing rollback
        if (random.nextInt(10) == 0) {
            logger.error("FLUTTERWAVE: Payment processing FAILED for account {}", accountNumber);
            throw new PaymentProcessingException("Flutterwave payment failed: Insufficient funds or network error");
        }

        logger.info("FLUTTERWAVE: Payment processed SUCCESSFULLY");
        logger.info("===========================================");
    }
}
