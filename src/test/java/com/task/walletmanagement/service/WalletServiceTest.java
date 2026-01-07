package com.task.walletmanagement.service;

import com.task.walletmanagement.dto.CreateWalletRequest;
import com.task.walletmanagement.dto.WalletResponse;
import com.task.walletmanagement.entity.Wallet;
import com.task.walletmanagement.exception.DuplicateEmailException;
import com.task.walletmanagement.exception.WalletNotFoundException;
import com.task.walletmanagement.repository.WalletRepository;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for WalletService.
 */
@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private CreateWalletRequest validRequest;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        validRequest = new CreateWalletRequest("test@example.com", "+1234567890");

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setEmail("test@example.com");
        wallet.setPhoneNumber("+1234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createWallet_Success() {
        when(walletRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.createWallet(validRequest);

        assertNotNull(response);
        assertEquals(wallet.getId(), response.getId());
        assertEquals(wallet.getEmail(), response.getEmail());
        assertEquals(wallet.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(BigDecimal.ZERO, response.getBalance());

        verify(walletRepository).existsByEmail(validRequest.getEmail());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_DuplicateEmail_ThrowsException() {
        when(walletRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> {
            walletService.createWallet(validRequest);
        });

        verify(walletRepository).existsByEmail(validRequest.getEmail());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void getWalletByEmail_Success() {
        when(walletRepository.findByEmail(wallet.getEmail())).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getWalletByEmail(wallet.getEmail());

        assertNotNull(response);
        assertEquals(wallet.getId(), response.getId());
        assertEquals(wallet.getEmail(), response.getEmail());

        verify(walletRepository).findByEmail(wallet.getEmail());
    }

    @Test
    void getWalletByEmail_NotFound_ThrowsException() {
        String email = "notfound@example.com";
        when(walletRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getWalletByEmail(email);
        });

        verify(walletRepository).findByEmail(email);
    }

    @Test
    void getWalletById_Success() {
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletById(wallet.getId());

        assertNotNull(result);
        assertEquals(wallet.getId(), result.getId());

        verify(walletRepository).findById(wallet.getId());
    }

    @Test
    void getWalletById_NotFound_ThrowsException() {
        Long walletId = 999L;
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getWalletById(walletId);
        });

        verify(walletRepository).findById(walletId);
    }
}
