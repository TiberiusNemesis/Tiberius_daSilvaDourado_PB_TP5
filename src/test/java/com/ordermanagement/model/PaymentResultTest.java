package com.ordermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PaymentResultTest {

    private PaymentResult paymentResult;

    @Test
    @DisplayName("Should create successful payment result")
    void testSuccessfulPaymentResult() {
        String transactionId = "TRX123456";

        paymentResult = PaymentResult.success(transactionId);

        assertTrue(paymentResult.isSuccess());
        assertEquals(transactionId, paymentResult.getTransactionId());
        assertNull(paymentResult.getErrorMessage());
    }

    @Test
    @DisplayName("Should create failed payment result")
    void testFailedPaymentResult() {
        String errorMessage = "Card has insufficient funds";

        paymentResult = PaymentResult.failure(errorMessage);

        assertFalse(paymentResult.isSuccess());
        assertNull(paymentResult.getTransactionId());
        assertEquals(errorMessage, paymentResult.getErrorMessage());
    }

    @Test
    @DisplayName("Should create payment result with constructor")
    void testConstructor() {
        boolean success = true;
        String transactionId = "TRX789";
        String errorMessage = null;

        paymentResult = new PaymentResult(success, transactionId, errorMessage);

        assertTrue(paymentResult.isSuccess());
        assertEquals(transactionId, paymentResult.getTransactionId());
        assertNull(paymentResult.getErrorMessage());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        paymentResult = new PaymentResult(false, null, "Error occurred");

        assertFalse(paymentResult.isSuccess());
        assertNull(paymentResult.getTransactionId());
        assertEquals("Error occurred", paymentResult.getErrorMessage());
    }
}