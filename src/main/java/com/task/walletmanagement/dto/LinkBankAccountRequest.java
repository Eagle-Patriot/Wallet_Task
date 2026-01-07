package com.task.walletmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for linking a bank account to a wallet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to link a bank account to a wallet")
public class LinkBankAccountRequest {

    @NotBlank(message = "Account number is required")
    @Schema(description = "Bank account number", example = "1234567890")
    private String accountNumber;

    @NotBlank(message = "Account name is required")
    @Schema(description = "Account holder name", example = "Chris Joseph")
    private String accountName;

    @NotBlank(message = "Bank name is required")
    @Schema(description = "Bank name", example = "GTBank")
    private String bank;
}
