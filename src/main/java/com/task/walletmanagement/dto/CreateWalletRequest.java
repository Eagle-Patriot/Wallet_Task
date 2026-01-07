package com.task.walletmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new wallet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new wallet")
public class CreateWalletRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email address (must be unique)", example = "chris@example.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;
}
