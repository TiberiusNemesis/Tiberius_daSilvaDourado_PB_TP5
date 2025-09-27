package com.ordermanagement.model;

import com.ordermanagement.enums.PaymentMethod;
import com.ordermanagement.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
    }

    @Test
    @DisplayName("Should create payment with default constructor")
    void testDefaultConstructor() {
        assertNotNull(payment);
        assertNull(payment.getId());
        assertNull(payment.getOrder());
        assertNull(payment.getAmount());
        assertNull(payment.getMethod());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNull(payment.getTransactionId());
        assertNull(payment.getCard());
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    @DisplayName("Should create payment with order and amount")
    void testConstructorWithOrderAndAmount() {
        Order order = new Order();
        order.setId(1L);
        BigDecimal amount = new BigDecimal("100.00");
        PaymentMethod method = PaymentMethod.CREDIT_CARD;
        PaymentCard card = new PaymentCard();

        payment = new Payment(order, amount, method, card);

        assertEquals(order, payment.getOrder());
        assertEquals(amount, payment.getAmount());
        assertEquals(method, payment.getMethod());
        assertEquals(card, payment.getCard());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    @DisplayName("Should approve payment successfully")
    void testApprovePayment() {
        payment.setAmount(new BigDecimal("50.00"));
        payment.setMethod(PaymentMethod.CREDIT_CARD);

        payment.approve("TRX123456");

        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        assertEquals("TRX123456", payment.getTransactionId());
        assertNotNull(payment.getProcessedAt());
    }

    @Test
    @DisplayName("Should reject payment")
    void testRejectPayment() {
        payment.setAmount(new BigDecimal("50.00"));
        payment.setMethod(PaymentMethod.DEBIT_CARD);

        String errorMessage = "Insufficient funds";
        payment.reject(errorMessage);

        assertEquals(PaymentStatus.REJECTED, payment.getStatus());
        assertEquals(errorMessage, payment.getFailureReason());
        assertNotNull(payment.getProcessedAt());
    }



    @Test
    @DisplayName("Should handle payment with credit card")
    void testPaymentWithCreditCard() {
        PaymentCard card = new PaymentCard();
        card.setCardNumber("4111111111111111");
        card.setHolderName("John Doe");
        card.setCvv("123");
        card.setExpiryDate("12/25");
        card.setType(PaymentMethod.CREDIT_CARD);

        payment.setMethod(PaymentMethod.CREDIT_CARD);
        payment.setCard(card);

        assertEquals(PaymentMethod.CREDIT_CARD, payment.getMethod());
        assertEquals(card, payment.getCard());
    }

    @Test
    @DisplayName("Should handle payment with PIX")
    void testPaymentWithPix() {
        payment.setMethod(PaymentMethod.PIX);

        assertEquals(PaymentMethod.PIX, payment.getMethod());
    }


    @Test
    @DisplayName("Should set and get all payment properties")
    void testSettersAndGetters() {
        Long id = 1L;
        Order order = new Order();
        BigDecimal amount = new BigDecimal("200.00");
        PaymentMethod method = PaymentMethod.CASH;
        PaymentStatus status = PaymentStatus.APPROVED;
        String transactionId = "TRX999";
        String failureReason = "Error";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime processedAt = LocalDateTime.now();

        payment.setId(id);
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(status);
        payment.setTransactionId(transactionId);
        payment.setFailureReason(failureReason);
        payment.setCreatedAt(createdAt);
        payment.setProcessedAt(processedAt);

        assertEquals(id, payment.getId());
        assertEquals(order, payment.getOrder());
        assertEquals(amount, payment.getAmount());
        assertEquals(method, payment.getMethod());
        assertEquals(status, payment.getStatus());
        assertEquals(transactionId, payment.getTransactionId());
        assertEquals(failureReason, payment.getFailureReason());
        assertEquals(createdAt, payment.getCreatedAt());
        assertEquals(processedAt, payment.getProcessedAt());
    }


    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        payment.setAmount(null);
        payment.setMethod(null);
        payment.setTransactionId(null);

        assertNull(payment.getAmount());
        assertNull(payment.getMethod());
        assertNull(payment.getTransactionId());
    }
}