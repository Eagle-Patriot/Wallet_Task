package com.task.walletmanagement.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PalindromeChecker.
 */
class PalindromeCheckerTest {

    @Test
    void testPositivePalindrome() {
        assertTrue(PalindromeChecker.isPalindrome(121));
        assertTrue(PalindromeChecker.isPalindrome(12321));
        assertTrue(PalindromeChecker.isPalindrome(1));
        assertTrue(PalindromeChecker.isPalindrome(0));
        assertTrue(PalindromeChecker.isPalindrome(11));
        assertTrue(PalindromeChecker.isPalindrome(9));
    }

    @Test
    void testNegativeNumbers() {
        assertFalse(PalindromeChecker.isPalindrome(-121));
        assertFalse(PalindromeChecker.isPalindrome(-1));
    }

    @Test
    void testNumbersEndingInZero() {
        assertFalse(PalindromeChecker.isPalindrome(10));
        assertFalse(PalindromeChecker.isPalindrome(100));
        assertFalse(PalindromeChecker.isPalindrome(1000));
    }

    @Test
    void testNonPalindromes() {
        assertFalse(PalindromeChecker.isPalindrome(123));
        assertFalse(PalindromeChecker.isPalindrome(1234));
    }

    @Test
    void testLargeNumbers() {
        assertTrue(PalindromeChecker.isPalindrome(123454321));
        assertFalse(PalindromeChecker.isPalindrome(123456789));
    }

    @Test
    void testEdgeCases() {
        assertTrue(PalindromeChecker.isPalindrome(0));
        assertTrue(PalindromeChecker.isPalindrome(7));
        assertFalse(PalindromeChecker.isPalindrome(Integer.MAX_VALUE));
    }
}
