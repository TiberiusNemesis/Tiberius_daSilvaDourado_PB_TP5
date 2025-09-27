package com.ordermanagement.service;

import com.ordermanagement.model.*;
import com.ordermanagement.enums.PaymentMethod;
import com.ordermanagement.enums.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentApiClientTest {

    @Mock
    private PaymentApiClient paymentApiClient;

    private Payment payment;
    private PaymentCard paymentCard;
    private PaymentCard destinationCard;
    private Order order;
    private Customer customer;
    private Address address;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer("customer@example.com", "password", "John Customer", "555-1234");
        customer.setId(1L);

        address = new Address("Main St", "123", "Downtown", "City", "ST", "12345");
        address.setId(1L);

        order = new Order(customer, address);
        order.setId(1L);

        paymentCard = new PaymentCard("4111111111111111", "John Customer", "12/25", "123", PaymentMethod.CREDIT_CARD);
        paymentCard.setId(1L);

        destinationCard = new PaymentCard("5555555555554444", "Jane Seller", "12/25", "456", PaymentMethod.CREDIT_CARD);
        destinationCard.setId(2L);

        payment = new Payment(order, new BigDecimal("50.00"), PaymentMethod.CREDIT_CARD, paymentCard);
        payment.setId(1L);
    }

    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPaymentSuccess() {
        PaymentResult expectedResult = PaymentResult.success("TXN123456");
        when(paymentApiClient.processPayment(payment)).thenReturn(expectedResult);

        PaymentResult result = paymentApiClient.processPayment(payment);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("TXN123456", result.getTransactionId());
        assertNull(result.getErrorMessage());
        verify(paymentApiClient, times(1)).processPayment(payment);
    }

    @Test
    @DisplayName("Should handle payment processing failure")
    void testProcessPaymentFailure() {
        PaymentResult expectedResult = PaymentResult.failure("Insufficient funds");
        when(paymentApiClient.processPayment(payment)).thenReturn(expectedResult);

        PaymentResult result = paymentApiClient.processPayment(payment);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(result.getTransactionId());
        assertEquals("Insufficient funds", result.getErrorMessage());
        verify(paymentApiClient, times(1)).processPayment(payment);
    }

    @Test
    @DisplayName("Should handle different payment failure reasons")
    void testProcessPaymentVariousFailures() {
        String[] errorMessages = {
            "Card declined",
            "Invalid card number",
            "Expired card",
            "Network timeout",
            "Invalid CVV"
        };

        for (String errorMessage : errorMessages) {
            PaymentResult expectedResult = PaymentResult.failure(errorMessage);
            when(paymentApiClient.processPayment(payment)).thenReturn(expectedResult);

            PaymentResult result = paymentApiClient.processPayment(payment);

            assertNotNull(result);
            assertFalse(result.isSuccess());
            assertEquals(errorMessage, result.getErrorMessage());
        }

        verify(paymentApiClient, times(errorMessages.length)).processPayment(payment);
    }

    @Test
    @DisplayName("Should transfer funds successfully")
    void testTransferSuccess() {
        BigDecimal transferAmount = new BigDecimal("25.00");
        doNothing().when(paymentApiClient).transfer(destinationCard, transferAmount);

        assertDoesNotThrow(() -> paymentApiClient.transfer(destinationCard, transferAmount));
        verify(paymentApiClient, times(1)).transfer(destinationCard, transferAmount);
    }

    @Test
    @DisplayName("Should handle transfer failure")
    void testTransferFailure() {
        BigDecimal transferAmount = new BigDecimal("25.00");
        doThrow(new RuntimeException("Transfer failed")).when(paymentApiClient).transfer(destinationCard, transferAmount);

        assertThrows(RuntimeException.class, () -> paymentApiClient.transfer(destinationCard, transferAmount));
        verify(paymentApiClient, times(1)).transfer(destinationCard, transferAmount);
    }

    @Test
    @DisplayName("Should transfer different amounts")
    void testTransferDifferentAmounts() {
        BigDecimal[] amounts = {
            new BigDecimal("10.00"),
            new BigDecimal("100.00"),
            new BigDecimal("0.01"),
            new BigDecimal("999.99")
        };

        for (BigDecimal amount : amounts) {
            doNothing().when(paymentApiClient).transfer(destinationCard, amount);

            assertDoesNotThrow(() -> paymentApiClient.transfer(destinationCard, amount));
        }

        verify(paymentApiClient, times(amounts.length)).transfer(eq(destinationCard), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should validate card successfully")
    void testValidateCardSuccess() {
        when(paymentApiClient.validateCard(paymentCard)).thenReturn(true);

        boolean result = paymentApiClient.validateCard(paymentCard);

        assertTrue(result);
        verify(paymentApiClient, times(1)).validateCard(paymentCard);
    }

    @Test
    @DisplayName("Should handle card validation failure")
    void testValidateCardFailure() {
        when(paymentApiClient.validateCard(paymentCard)).thenReturn(false);

        boolean result = paymentApiClient.validateCard(paymentCard);

        assertFalse(result);
        verify(paymentApiClient, times(1)).validateCard(paymentCard);
    }

    @Test
    @DisplayName("Should validate different card types")
    void testValidateDifferentCardTypes() {
        PaymentCard creditCard = new PaymentCard("4111111111111111", "John Doe", "12/25", "123", PaymentMethod.CREDIT_CARD);
        PaymentCard debitCard = new PaymentCard("5555555555554444", "Jane Doe", "12/25", "456", PaymentMethod.DEBIT_CARD);

        when(paymentApiClient.validateCard(creditCard)).thenReturn(true);
        when(paymentApiClient.validateCard(debitCard)).thenReturn(true);

        assertTrue(paymentApiClient.validateCard(creditCard));
        assertTrue(paymentApiClient.validateCard(debitCard));

        verify(paymentApiClient, times(1)).validateCard(creditCard);
        verify(paymentApiClient, times(1)).validateCard(debitCard);
    }

    @Test
    @DisplayName("Should handle invalid card validation")
    void testValidateInvalidCards() {
        PaymentCard invalidCard1 = new PaymentCard("1234567890123456", "Invalid User", "01/20", "000", PaymentMethod.CREDIT_CARD); // Expired
        PaymentCard invalidCard2 = new PaymentCard("0000000000000000", "Invalid User", "12/25", "123", PaymentMethod.CREDIT_CARD); // Invalid number

        when(paymentApiClient.validateCard(invalidCard1)).thenReturn(false);
        when(paymentApiClient.validateCard(invalidCard2)).thenReturn(false);

        assertFalse(paymentApiClient.validateCard(invalidCard1));
        assertFalse(paymentApiClient.validateCard(invalidCard2));

        verify(paymentApiClient, times(1)).validateCard(invalidCard1);
        verify(paymentApiClient, times(1)).validateCard(invalidCard2);
    }

    @Test
    @DisplayName("Should handle null payment in processPayment")
    void testProcessPaymentWithNull() {
        when(paymentApiClient.processPayment(null)).thenThrow(new IllegalArgumentException("Payment cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> paymentApiClient.processPayment(null));
        verify(paymentApiClient, times(1)).processPayment(null);
    }

    @Test
    @DisplayName("Should handle null card in transfer")
    void testTransferWithNullCard() {
        BigDecimal amount = new BigDecimal("25.00");
        doThrow(new IllegalArgumentException("Destination card cannot be null"))
            .when(paymentApiClient).transfer(null, amount);

        assertThrows(IllegalArgumentException.class, () -> paymentApiClient.transfer(null, amount));
        verify(paymentApiClient, times(1)).transfer(null, amount);
    }

    @Test
    @DisplayName("Should handle null amount in transfer")
    void testTransferWithNullAmount() {
        doThrow(new IllegalArgumentException("Amount cannot be null"))
            .when(paymentApiClient).transfer(destinationCard, null);

        assertThrows(IllegalArgumentException.class, () -> paymentApiClient.transfer(destinationCard, null));
        verify(paymentApiClient, times(1)).transfer(destinationCard, null);
    }

    @Test
    @DisplayName("Should handle null card in validateCard")
    void testValidateNullCard() {
        when(paymentApiClient.validateCard(null)).thenThrow(new IllegalArgumentException("Card cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> paymentApiClient.validateCard(null));
        verify(paymentApiClient, times(1)).validateCard(null);
    }
}