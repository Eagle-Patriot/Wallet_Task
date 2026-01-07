package com.task.walletmanagement.controller;

import com.task.walletmanagement.dto.*;
import com.task.walletmanagement.service.BankAccountService;
import com.task.walletmanagement.service.PaymentService;
import com.task.walletmanagement.service.TransactionService;
import com.task.walletmanagement.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for wallet management operations.
 */
@RestController
@RequestMapping("/api/wallets")
@Tag(name = "Wallet Management", description = "APIs for managing wallets, bank accounts, and transactions")
public class WalletController {

        @Autowired
        private WalletService walletService;
        @Autowired
        private BankAccountService bankAccountService;
        @Autowired
        private PaymentService paymentService;
        @Autowired
        private TransactionService transactionService;

        /**
         * Create a new wallet.
         */
        @PostMapping
        @Operation(summary = "Create wallet", description = "Create a new wallet with email and phone number")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Wallet created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "409", description = "Email already exists")
        })
        public ResponseEntity<WalletResponse> createWallet(
                        @Valid @RequestBody CreateWalletRequest request) {
                WalletResponse response = walletService.createWallet(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Get wallet by email.
         */
        @GetMapping
        @Operation(summary = "Get wallet by email", description = "Retrieve wallet information by email address")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Wallet found"),
                        @ApiResponse(responseCode = "404", description = "Wallet not found")
        })
        public ResponseEntity<WalletResponse> getWalletByEmail(
                        @Parameter(description = "Email address", required = true) @RequestParam String email) {
                WalletResponse response = walletService.getWalletByEmail(email);
                return ResponseEntity.ok(response);
        }

        /**
         * Link a bank account to a wallet.
         */
        @PostMapping("/{walletId}/bank-accounts")
        @Operation(summary = "Link bank account", description = "Link a bank account to a wallet")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Bank account linked successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "404", description = "Wallet not found"),
                        @ApiResponse(responseCode = "409", description = "Bank account already exists")
        })
        public ResponseEntity<BankAccountResponse> linkBankAccount(
                        @Parameter(description = "Wallet ID", required = true) @PathVariable Long walletId,
                        @Valid @RequestBody LinkBankAccountRequest request) {
                BankAccountResponse response = bankAccountService.linkBankAccount(walletId, request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Get all bank accounts linked to a wallet.
         */
        @GetMapping("/{walletId}/bank-accounts")
        @Operation(summary = "Get bank accounts", description = "Get all bank accounts linked to a wallet")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Bank accounts retrieved"),
                        @ApiResponse(responseCode = "404", description = "Wallet not found")
        })
        public ResponseEntity<List<BankAccountResponse>> getBankAccounts(
                        @Parameter(description = "Wallet ID", required = true) @PathVariable Long walletId) {
                List<BankAccountResponse> response = bankAccountService.getBankAccountsByWalletId(walletId);
                return ResponseEntity.ok(response);
        }

        /**
         * Fund a wallet from a linked bank account via payment gateway.
         */
        @PostMapping("/{walletId}/fund")
        @Operation(summary = "Fund wallet", description = "Fund a wallet from a linked bank account via payment gateway (Flutterwave or Paystack)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Wallet funded successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input or payment failed"),
                        @ApiResponse(responseCode = "404", description = "Wallet or bank account not found")
        })
        public ResponseEntity<TransactionResponse> fundWallet(
                        @Parameter(description = "Wallet ID", required = true) @PathVariable Long walletId,
                        @Valid @RequestBody FundWalletRequest request) {
                TransactionResponse response = paymentService.fundWallet(walletId, request);
                return ResponseEntity.ok(response);
        }

        /**
         * Get all transactions for a wallet.
         */
        @GetMapping("/{walletId}/transactions")
        @Operation(summary = "Get transactions", description = "Get all transactions for a wallet, ordered by date (newest first)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Transactions retrieved"),
                        @ApiResponse(responseCode = "404", description = "Wallet not found")
        })
        public ResponseEntity<List<TransactionResponse>> getTransactions(
                        @Parameter(description = "Wallet ID", required = true) @PathVariable Long walletId) {
                List<TransactionResponse> response = transactionService.getTransactionsByWalletId(walletId);
                return ResponseEntity.ok(response);
        }
}
