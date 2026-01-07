package com.task.walletmanagement.dto;

import com.task.walletmanagement.enums.PaymentGateway;
import com.task.walletmanagement.enums.TransactionStatus;
import com.task.walletmanagement.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for transaction information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Transaction information")
public class TransactionResponse {

    @Schema(description = "Transaction ID", example = "1")
    private Long id;

    @Schema(description = "Transaction amount", example = "1000.00")
    private BigDecimal amount;

    @Schema(description = "Transaction type (CREDIT/DEBIT)", example = "CREDIT")
    private TransactionType type;

    @Schema(description = "Transaction description", example = "Wallet funded via FLUTTERWAVE")
    private String description;

    @Schema(description = "Payment gateway used", example = "FLUTTERWAVE")
    private PaymentGateway paymentGateway;

    @Schema(description = "Transaction status (SUCCESS/FAILED)", example = "SUCCESS")
    private TransactionStatus status;

    @Schema(description = "Transaction timestamp")
    private LocalDateTime createdAt;
}
