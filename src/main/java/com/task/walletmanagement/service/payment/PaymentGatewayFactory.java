package com.task.walletmanagement.service.payment;

import com.task.walletmanagement.enums.PaymentGateway;
import org.springframework.stereotype.Component;

/**
 * Factory for creating payment gateway strategies.
 */
@Component
public class PaymentGatewayFactory {

    private final FlutterwavePaymentStrategy flutterwaveStrategy;
    private final PaystackPaymentStrategy paystackStrategy;

    public PaymentGatewayFactory(FlutterwavePaymentStrategy flutterwaveStrategy,
            PaystackPaymentStrategy paystackStrategy) {
        this.flutterwaveStrategy = flutterwaveStrategy;
        this.paystackStrategy = paystackStrategy;
    }

    /**
     * Get the appropriate payment gateway strategy based on the payment gateway
     * enum.
     */
    public PaymentGatewayStrategy getStrategy(PaymentGateway paymentGateway) {
        return switch (paymentGateway) {
            case FLUTTERWAVE -> flutterwaveStrategy;
            case PAYSTACK -> paystackStrategy;
        };
    }
}
