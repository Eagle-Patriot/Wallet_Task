package com.task.walletmanagement.service;

import com.task.walletmanagement.dto.BankAccountResponse;
import com.task.walletmanagement.dto.LinkBankAccountRequest;
import com.task.walletmanagement.entity.BankAccount;
import com.task.walletmanagement.entity.Wallet;
import com.task.walletmanagement.exception.DuplicateBankAccountException;
import com.task.walletmanagement.exception.WalletNotFoundException;
import com.task.walletmanagement.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BankAccountService.
 */
@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private BankAccountService bankAccountService;

    private Wallet wallet;
    private BankAccount bankAccount;
    private LinkBankAccountRequest validRequest;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setEmail("test@example.com");
        wallet.setPhoneNumber("+1234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());

        bankAccount = new BankAccount();
        bankAccount.setId(1L);
        bankAccount.setWallet(wallet);
        bankAccount.setAccountNumber("1234567890");
        bankAccount.setAccountName("Chris Joseph");
        bankAccount.setBank("GTBank");
        bankAccount.setCreatedAt(LocalDateTime.now());

        validRequest = new LinkBankAccountRequest("1234567890", "Chris Joseph", "GTBank");
    }

    @Test
    void linkBankAccount_Success() {
        when(walletService.getWalletById(wallet.getId())).thenReturn(wallet);
        when(bankAccountRepository.findByAccountNumberAndBank(validRequest.getAccountNumber(), validRequest.getBank()))
                .thenReturn(Optional.empty());
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccountResponse response = bankAccountService.linkBankAccount(wallet.getId(), validRequest);

        assertNotNull(response);
        assertEquals(bankAccount.getId(), response.getId());
        assertEquals(bankAccount.getAccountNumber(), response.getAccountNumber());
        assertEquals(bankAccount.getAccountName(), response.getAccountName());
        assertEquals(bankAccount.getBank(), response.getBank());

        verify(walletService).getWalletById(wallet.getId());
        verify(bankAccountRepository).findByAccountNumberAndBank(validRequest.getAccountNumber(),
                validRequest.getBank());
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void linkBankAccount_WalletNotFound_ThrowsException() {
        Long invalidWalletId = 999L;
        when(walletService.getWalletById(invalidWalletId))
                .thenThrow(new WalletNotFoundException("Wallet not found with ID: " + invalidWalletId));

        assertThrows(WalletNotFoundException.class, () -> {
            bankAccountService.linkBankAccount(invalidWalletId, validRequest);
        });

        verify(walletService).getWalletById(invalidWalletId);
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void linkBankAccount_DuplicateAccount_ThrowsException() {
        when(walletService.getWalletById(wallet.getId())).thenReturn(wallet);
        when(bankAccountRepository.findByAccountNumberAndBank(validRequest.getAccountNumber(), validRequest.getBank()))
                .thenReturn(Optional.of(bankAccount));

        assertThrows(DuplicateBankAccountException.class, () -> {
            bankAccountService.linkBankAccount(wallet.getId(), validRequest);
        });

        verify(walletService).getWalletById(wallet.getId());
        verify(bankAccountRepository).findByAccountNumberAndBank(validRequest.getAccountNumber(),
                validRequest.getBank());
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void getBankAccountsByWalletId_Success() {
        BankAccount account2 = new BankAccount();
        account2.setId(2L);
        account2.setWallet(wallet);
        account2.setAccountNumber("0987654321");
        account2.setAccountName("Chris Joseph");
        account2.setBank("Access Bank");
        account2.setCreatedAt(LocalDateTime.now());

        when(walletService.getWalletById(wallet.getId())).thenReturn(wallet);
        when(bankAccountRepository.findByWalletId(wallet.getId()))
                .thenReturn(Arrays.asList(bankAccount, account2));

        List<BankAccountResponse> responses = bankAccountService.getBankAccountsByWalletId(wallet.getId());

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(bankAccount.getAccountNumber(), responses.get(0).getAccountNumber());
        assertEquals(account2.getAccountNumber(), responses.get(1).getAccountNumber());

        verify(walletService).getWalletById(wallet.getId());
        verify(bankAccountRepository).findByWalletId(wallet.getId());
    }

    @Test
    void getBankAccountsByWalletId_EmptyList() {
        when(walletService.getWalletById(wallet.getId())).thenReturn(wallet);
        when(bankAccountRepository.findByWalletId(wallet.getId())).thenReturn(Arrays.asList());

        List<BankAccountResponse> responses = bankAccountService.getBankAccountsByWalletId(wallet.getId());

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(walletService).getWalletById(wallet.getId());
        verify(bankAccountRepository).findByWalletId(wallet.getId());
    }

    @Test
    void getBankAccountByAccountNumberAndWallet_Success() {
        when(bankAccountRepository.findByAccountNumberAndBankAndWalletId(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId()))
                .thenReturn(Optional.of(bankAccount));

        BankAccount result = bankAccountService.getBankAccountByAccountNumberAndWallet(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId());

        assertNotNull(result);
        assertEquals(bankAccount.getId(), result.getId());
        assertEquals(bankAccount.getAccountNumber(), result.getAccountNumber());

        verify(bankAccountRepository).findByAccountNumberAndBankAndWalletId(
                bankAccount.getAccountNumber(),
                bankAccount.getBank(),
                wallet.getId());
    }
}
