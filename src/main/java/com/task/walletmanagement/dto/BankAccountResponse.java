package com.task.walletmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for bank account information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bank account information")
public class BankAccountResponse {

    @Schema(description = "Bank account ID", example = "1")
    private Long id;

    @Schema(description = "Account number", example = "1234567890")
    private String accountNumber;

    @Schema(description = "Account holder name", example = "Chris Joseph")
    private String accountName;

    @Schema(description = "Bank name", example = "GTBank")
    private String bank;

    @Schema(description = "Account linkage timestamp")
    private LocalDateTime createdAt;
}
