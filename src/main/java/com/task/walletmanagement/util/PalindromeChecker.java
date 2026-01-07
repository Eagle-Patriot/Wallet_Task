package com.task.walletmanagement.util;

public class PalindromeChecker {

    /**
     * A palindrome reads the same forward and backward.
     * 
     * @param x the integer to check
     * @return true if x is a palindrome, false otherwise
     */
    public static boolean isPalindrome(int x) {
        // Negative numbers are not palindromes
        // Numbers ending in 0 (except 0 itself) are not palindromes
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }

        // Single digit numbers are palindromes
        if (x < 10) {
            return true;
        }

        // Reverse half of the number and compare
        int reversedHalf = 0;
        while (x > reversedHalf) {
            reversedHalf = reversedHalf * 10 + x % 10;
            x /= 10;
        }

        // For even length numbers: x == reversedHalf
        // For odd length numbers: x == reversedHalf / 10 (middle digit doesn't matter)
        return x == reversedHalf || x == reversedHalf / 10;
    }
}
