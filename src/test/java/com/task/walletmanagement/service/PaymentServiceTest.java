package com.task.walletmanagement.service;

import com.task.walletmanagement.dto.FundWalletRequest;
import com.task.walletmanagement.dto.TransactionResponse;
import com.task.walletmanagement.entity.BankAccount;
import com.task.walletmanagement.entity.Transaction;
import com.task.walletmanagement.entity.Wallet;
import com.task.walletmanagement.enums.PaymentGateway;
import com.task.walletmanagement.enums.TransactionStatus;
import com.task.walletmanagement.enums.TransactionType;
import com.task.walletmanagement.exception.PaymentProcessingException;
import com.task.walletmanagement.repository.WalletRepository;
import com.task.walletmanagement.service.payment.PaymentGatewayFactory;
import com.task.walletmanagement.service.payment.PaymentGatewayStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentService, focusing on atomic wallet funding operations.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private PaymentGatewayFactory paymentGatewayFactory;

    @Mock
    private PaymentGatewayStrategy paymentGatewayStrategy;

    @InjectMocks
    private PaymentService paymentService;

    private Wallet wallet;
    private BankAccount bankAccount;
    private FundWalletRequest fundRequest;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setEmail("test@example.com");
        wallet.setPhoneNumber("+1234567890");
        wallet.setBalance(new BigDecimal("1000.00"));
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());

        bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setWallet(wallet);
        bankAccount.setAccountNumber("1234567890");
        bankAccount.setAccountName("Chris Joseph");
        bankAccount.setBank("GTBank");
        bankAccount.setCreatedAt(LocalDateTime.now());

        fundRequest = new FundWalletRequest(
                "1234567890",
                new BigDecimal("5000.00"),
                PaymentGateway.FLUTTERWAVE);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setWallet(wallet);
        transaction.setAmount(new BigDecimal("5000.00"));
        transaction.setType(TransactionType.CREDIT);
        transaction.setDescription("Wallet funding via FLUTTERWAVE");
        transaction.setPaymentGateway(PaymentGateway.FLUTTERWAVE);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void fundWallet_Success_FlutterwaveGateway() {
        when(walletRepository.findByIdWithLock(wallet.getId())).thenReturn(Optional.of(wallet));
        when(bankAccountService.getBankAccountsByWalletId(wallet.getId()))
                .thenReturn(java.util.Arrays.asList(mapToBankAccountResponse(bankAccount)));
        when(bankAccountService.getBankAccountByAccountNumberAndWallet(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId()))
                .thenReturn(bankAccount);
        when(paymentGatewayFactory.getStrategy(PaymentGateway.FLUTTERWAVE))
                .thenReturn(paymentGatewayStrategy);
        doNothing().when(paymentGatewayStrategy).processPayment(fundRequest.getAccountNumber(),
                fundRequest.getAmount());
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionService.createTransaction(
                eq(wallet),
                eq(fundRequest.getAmount()),
                eq(TransactionType.CREDIT),
                any(String.class),
                eq(fundRequest.getPaymentGateway()),
                eq(TransactionStatus.SUCCESS)))
                .thenReturn(transaction);

        TransactionResponse response = paymentService.fundWallet(wallet.getId(), fundRequest);

        assertNotNull(response);
        assertEquals(transaction.getId(), response.getId());
        assertEquals(transaction.getAmount(), response.getAmount());
        assertEquals(TransactionType.CREDIT, response.getType());
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());

        verify(walletRepository).findByIdWithLock(wallet.getId());
        verify(paymentGatewayStrategy).processPayment(fundRequest.getAccountNumber(), fundRequest.getAmount());
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionService).createTransaction(
                eq(wallet),
                eq(fundRequest.getAmount()),
                eq(TransactionType.CREDIT),
                any(String.class),
                eq(fundRequest.getPaymentGateway()),
                eq(TransactionStatus.SUCCESS));
    }

    @Test
    void fundWallet_Success_PaystackGateway() {
        fundRequest = new FundWalletRequest(
                "1234567890",
                new BigDecimal("3000.00"),
                PaymentGateway.PAYSTACK);

        when(walletRepository.findByIdWithLock(wallet.getId())).thenReturn(Optional.of(wallet));
        when(bankAccountService.getBankAccountsByWalletId(wallet.getId()))
                .thenReturn(java.util.Arrays.asList(mapToBankAccountResponse(bankAccount)));
        when(bankAccountService.getBankAccountByAccountNumberAndWallet(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId()))
                .thenReturn(bankAccount);
        when(paymentGatewayFactory.getStrategy(PaymentGateway.PAYSTACK))
                .thenReturn(paymentGatewayStrategy);
        doNothing().when(paymentGatewayStrategy).processPayment(fundRequest.getAccountNumber(),
                fundRequest.getAmount());
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionService.createTransaction(
                eq(wallet),
                eq(fundRequest.getAmount()),
                eq(TransactionType.CREDIT),
                any(String.class),
                eq(fundRequest.getPaymentGateway()),
                eq(TransactionStatus.SUCCESS)))
                .thenReturn(transaction);

        TransactionResponse response = paymentService.fundWallet(wallet.getId(), fundRequest);

        assertNotNull(response);
        verify(paymentGatewayFactory).getStrategy(PaymentGateway.PAYSTACK);
        verify(paymentGatewayStrategy).processPayment(fundRequest.getAccountNumber(), fundRequest.getAmount());
    }

    @Test
    void fundWallet_WalletNotFound_ThrowsException() {
        Long invalidWalletId = 999L;
        when(walletRepository.findByIdWithLock(invalidWalletId)).thenReturn(Optional.empty());

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            paymentService.fundWallet(invalidWalletId, fundRequest);
        });

        assertTrue(exception.getMessage().contains("Wallet not found"));
        verify(walletRepository).findByIdWithLock(invalidWalletId);
        verify(paymentGatewayStrategy, never()).processPayment(any(), any());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void fundWallet_BankAccountNotLinked_ThrowsException() {
        when(walletRepository.findByIdWithLock(wallet.getId())).thenReturn(Optional.of(wallet));
        when(bankAccountService.getBankAccountsByWalletId(wallet.getId()))
                .thenReturn(java.util.Arrays.asList());

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            paymentService.fundWallet(wallet.getId(), fundRequest);
        });

        assertTrue(exception.getMessage().contains("not linked to this wallet"));
        verify(walletRepository).findByIdWithLock(wallet.getId());
        verify(paymentGatewayStrategy, never()).processPayment(any(), any());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void fundWallet_PaymentGatewayFails_ThrowsException() {
        when(walletRepository.findByIdWithLock(wallet.getId())).thenReturn(Optional.of(wallet));
        when(bankAccountService.getBankAccountsByWalletId(wallet.getId()))
                .thenReturn(java.util.Arrays.asList(mapToBankAccountResponse(bankAccount)));
        when(bankAccountService.getBankAccountByAccountNumberAndWallet(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId()))
                .thenReturn(bankAccount);
        when(paymentGatewayFactory.getStrategy(PaymentGateway.FLUTTERWAVE))
                .thenReturn(paymentGatewayStrategy);
        doThrow(new PaymentProcessingException("Insufficient funds"))
                .when(paymentGatewayStrategy).processPayment(fundRequest.getAccountNumber(), fundRequest.getAmount());

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            paymentService.fundWallet(wallet.getId(), fundRequest);
        });

        assertEquals("Insufficient funds", exception.getMessage());
        verify(walletRepository).findByIdWithLock(wallet.getId());
        verify(paymentGatewayStrategy).processPayment(fundRequest.getAccountNumber(), fundRequest.getAmount());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void fundWallet_BalanceUpdatedCorrectly() {
        BigDecimal initialBalance = wallet.getBalance();
        BigDecimal fundingAmount = fundRequest.getAmount();

        when(walletRepository.findByIdWithLock(wallet.getId())).thenReturn(Optional.of(wallet));
        when(bankAccountService.getBankAccountsByWalletId(wallet.getId()))
                .thenReturn(java.util.Arrays.asList(mapToBankAccountResponse(bankAccount)));
        when(bankAccountService.getBankAccountByAccountNumberAndWallet(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId()))
                .thenReturn(bankAccount);
        when(paymentGatewayFactory.getStrategy(PaymentGateway.FLUTTERWAVE))
                .thenReturn(paymentGatewayStrategy);
        doNothing().when(paymentGatewayStrategy).processPayment(fundRequest.getAccountNumber(),
                fundRequest.getAmount());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet savedWallet = invocation.getArgument(0);
            assertEquals(initialBalance.add(fundingAmount), savedWallet.getBalance());
            return savedWallet;
        });
        when(transactionService.createTransaction(
                eq(wallet),
                eq(fundRequest.getAmount()),
                eq(TransactionType.CREDIT),
                any(String.class),
                eq(fundRequest.getPaymentGateway()),
                eq(TransactionStatus.SUCCESS)))
                .thenReturn(transaction);

        paymentService.fundWallet(wallet.getId(), fundRequest);

        verify(walletRepository).save(argThat(w -> w.getBalance().compareTo(initialBalance.add(fundingAmount)) == 0));
    }

    /**
     * This test shows that new payment providers can be added without modifying
     * PaymentService
     */
    @Test
    void fundWallet_NewPaymentGateway_DemonstratesExtensibility() {
        // Simulate adding a new payment gateway (e.g., Stripe)

        fundRequest = new FundWalletRequest(
                "1234567890",
                new BigDecimal("2500.00"),
                PaymentGateway.PAYSTACK); // Using Paystack as proxy for "new" gateway

        when(walletRepository.findByIdWithLock(wallet.getId())).thenReturn(Optional.of(wallet));
        when(bankAccountService.getBankAccountsByWalletId(wallet.getId()))
                .thenReturn(java.util.Arrays.asList(mapToBankAccountResponse(bankAccount)));
        when(bankAccountService.getBankAccountByAccountNumberAndWallet(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId()))
                .thenReturn(bankAccount);

        PaymentGatewayStrategy newGatewayStrategy = mock(PaymentGatewayStrategy.class);
        when(paymentGatewayFactory.getStrategy(PaymentGateway.PAYSTACK))
                .thenReturn(newGatewayStrategy);
        doNothing().when(newGatewayStrategy).processPayment(fundRequest.getAccountNumber(), fundRequest.getAmount());

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionService.createTransaction(
                eq(wallet),
                eq(fundRequest.getAmount()),
                eq(TransactionType.CREDIT),
                any(String.class),
                eq(fundRequest.getPaymentGateway()),
                eq(TransactionStatus.SUCCESS)))
                .thenReturn(transaction);

        TransactionResponse response = paymentService.fundWallet(wallet.getId(), fundRequest);

        // Verify the new gateway was used successfully
        assertNotNull(response);
        verify(paymentGatewayFactory).getStrategy(PaymentGateway.PAYSTACK);
        verify(newGatewayStrategy).processPayment(fundRequest.getAccountNumber(), fundRequest.getAmount());
        verify(walletRepository).save(any(Wallet.class));
    }

    private com.task.walletmanagement.dto.BankAccountResponse mapToBankAccountResponse(BankAccount account) {
        com.task.walletmanagement.dto.BankAccountResponse response = new com.task.walletmanagement.dto.BankAccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountName(account.getAccountName());
        response.setBank(account.getBank());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }
}
