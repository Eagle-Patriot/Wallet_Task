package com.task.walletmanagement.dto;

import com.task.walletmanagement.enums.PaymentGateway;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for funding a wallet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to fund a wallet from a linked bank account")
public class FundWalletRequest {

    @NotBlank(message = "Account number is required")
    @Schema(description = "Bank account number to fund from", example = "1234567890")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Schema(description = "Amount to fund", example = "1000.00")
    private BigDecimal amount;

    @NotNull(message = "Payment gateway is required")
    @Schema(description = "Payment gateway to use (FLUTTERWAVE or PAYSTACK)", example = "FLUTTERWAVE")
    private PaymentGateway paymentGateway;
}
