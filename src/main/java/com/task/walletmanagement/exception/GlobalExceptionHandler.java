package com.task.walletmanagement.exception;

import com.task.walletmanagement.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Handle wallet not found exceptions.
         */
        @ExceptionHandler(WalletNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleWalletNotFound(
                        WalletNotFoundException ex, HttpServletRequest request) {
                logger.error("Wallet not found: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handle duplicate email exceptions.
         */
        @ExceptionHandler(DuplicateEmailException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateEmail(
                        DuplicateEmailException ex, HttpServletRequest request) {
                logger.error("Duplicate email: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handle bank account not found exceptions.
         */
        @ExceptionHandler(BankAccountNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleBankAccountNotFound(
                        BankAccountNotFoundException ex, HttpServletRequest request) {
                logger.error("Bank account not found: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Handle duplicate bank account exceptions.
         */
        @ExceptionHandler(DuplicateBankAccountException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateBankAccount(
                        DuplicateBankAccountException ex, HttpServletRequest request) {
                logger.error("Duplicate bank account: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Handle payment processing exceptions.
         */
        @ExceptionHandler(PaymentProcessingException.class)
        public ResponseEntity<ErrorResponse> handlePaymentProcessing(
                        PaymentProcessingException ex, HttpServletRequest request) {
                logger.error("Payment processing failed: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Handle validation errors from @Valid annotations.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                logger.error("Validation failed: {}", errors);

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "Validation failed: " + errors.toString(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Handle all other uncaught exceptions.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                logger.error("Unexpected error occurred", ex);

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "An unexpected error occurred: " + ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
}
