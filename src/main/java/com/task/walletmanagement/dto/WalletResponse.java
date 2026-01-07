package com.task.walletmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for wallet information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wallet information")
public class WalletResponse {

    @Schema(description = "Wallet ID", example = "1")
    private Long id;

    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Current wallet balance", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "Wallet creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
