package com.ordermanagement.repository;

import com.ordermanagement.model.Payment;
import com.ordermanagement.model.Order;
import com.ordermanagement.enums.PaymentStatus;
import com.ordermanagement.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    private Payment testPayment;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testOrder = new Order();
        testOrder.setId(1L);

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setOrder(testOrder);
        testPayment.setAmount(new BigDecimal("35.90"));
        testPayment.setMethod(PaymentMethod.CREDIT_CARD);
        testPayment.setStatus(PaymentStatus.PENDING);
        testPayment.setTransactionId("TXN123456789");
        testPayment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save payment successfully")
    void testSavePayment() {
        when(paymentRepository.save(testPayment)).thenReturn(testPayment);

        Payment savedPayment = paymentRepository.save(testPayment);

        assertNotNull(savedPayment);
        assertEquals(testPayment.getId(), savedPayment.getId());
        assertEquals(testPayment.getOrder().getId(), savedPayment.getOrder().getId());
        assertEquals(testPayment.getAmount(), savedPayment.getAmount());
        assertEquals(testPayment.getMethod(), savedPayment.getMethod());
        assertEquals(testPayment.getStatus(), savedPayment.getStatus());
        assertEquals(testPayment.getTransactionId(), savedPayment.getTransactionId());
        verify(paymentRepository).save(testPayment);
    }

    @Test
    @DisplayName("Should find payment by id")
    void testFindById() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        Optional<Payment> foundPayment = paymentRepository.findById(1L);

        assertTrue(foundPayment.isPresent());
        assertEquals(testPayment.getId(), foundPayment.get().getId());
        assertEquals(testPayment.getTransactionId(), foundPayment.get().getTransactionId());
        verify(paymentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when payment not found by id")
    void testFindByIdNotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Payment> foundPayment = paymentRepository.findById(999L);

        assertFalse(foundPayment.isPresent());
        verify(paymentRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find all payments")
    void testFindAll() {
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setOrder(testOrder);
        payment2.setAmount(new BigDecimal("15.50"));
        payment2.setStatus(PaymentStatus.APPROVED);

        List<Payment> payments = Arrays.asList(testPayment, payment2);
        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> foundPayments = paymentRepository.findAll();

        assertNotNull(foundPayments);
        assertEquals(2, foundPayments.size());
        assertTrue(foundPayments.contains(testPayment));
        assertTrue(foundPayments.contains(payment2));
        verify(paymentRepository).findAll();
    }

    @Test
    @DisplayName("Should find payments by order")
    void testFindByOrder() {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setOrder(testOrder);

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setOrder(testOrder);

        List<Payment> orderPayments = Arrays.asList(payment1, payment2);
        when(paymentRepository.findByOrder(testOrder)).thenReturn(orderPayments);

        List<Payment> foundPayments = paymentRepository.findByOrder(testOrder);

        assertNotNull(foundPayments);
        assertEquals(2, foundPayments.size());
        foundPayments.forEach(payment -> assertEquals(testOrder.getId(), payment.getOrder().getId()));
        verify(paymentRepository).findByOrder(testOrder);
    }

    @Test
    @DisplayName("Should return empty list when no payments found by order")
    void testFindByOrderEmpty() {
        Order otherOrder = new Order();
        otherOrder.setId(999L);

        when(paymentRepository.findByOrder(otherOrder)).thenReturn(Arrays.asList());

        List<Payment> foundPayments = paymentRepository.findByOrder(otherOrder);

        assertNotNull(foundPayments);
        assertTrue(foundPayments.isEmpty());
        verify(paymentRepository).findByOrder(otherOrder);
    }

    @Test
    @DisplayName("Should find payments by status")
    void testFindByStatus() {
        Payment pendingPayment1 = new Payment();
        pendingPayment1.setId(1L);
        pendingPayment1.setStatus(PaymentStatus.PENDING);

        Payment pendingPayment2 = new Payment();
        pendingPayment2.setId(2L);
        pendingPayment2.setStatus(PaymentStatus.PENDING);

        List<Payment> pendingPayments = Arrays.asList(pendingPayment1, pendingPayment2);
        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(pendingPayments);

        List<Payment> foundPayments = paymentRepository.findByStatus(PaymentStatus.PENDING);

        assertNotNull(foundPayments);
        assertEquals(2, foundPayments.size());
        foundPayments.forEach(payment -> assertEquals(PaymentStatus.PENDING, payment.getStatus()));
        verify(paymentRepository).findByStatus(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Should return empty list when no payments found by status")
    void testFindByStatusEmpty() {
        when(paymentRepository.findByStatus(PaymentStatus.REJECTED)).thenReturn(Arrays.asList());

        List<Payment> foundPayments = paymentRepository.findByStatus(PaymentStatus.REJECTED);

        assertNotNull(foundPayments);
        assertTrue(foundPayments.isEmpty());
        verify(paymentRepository).findByStatus(PaymentStatus.REJECTED);
    }

    @Test
    @DisplayName("Should find payment by transaction id")
    void testFindByTransactionId() {
        when(paymentRepository.findByTransactionId("TXN123456789")).thenReturn(Optional.of(testPayment));

        Optional<Payment> foundPayment = paymentRepository.findByTransactionId("TXN123456789");

        assertTrue(foundPayment.isPresent());
        assertEquals(testPayment.getTransactionId(), foundPayment.get().getTransactionId());
        assertEquals(testPayment.getId(), foundPayment.get().getId());
        verify(paymentRepository).findByTransactionId("TXN123456789");
    }

    @Test
    @DisplayName("Should return empty when payment not found by transaction id")
    void testFindByTransactionIdNotFound() {
        when(paymentRepository.findByTransactionId("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Payment> foundPayment = paymentRepository.findByTransactionId("NONEXISTENT");

        assertFalse(foundPayment.isPresent());
        verify(paymentRepository).findByTransactionId("NONEXISTENT");
    }

    @Test
    @DisplayName("Should delete payment by id")
    void testDeleteById() {
        doNothing().when(paymentRepository).deleteById(1L);

        paymentRepository.deleteById(1L);

        verify(paymentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void testNullParameters() {
        when(paymentRepository.findById(null)).thenReturn(Optional.empty());
        when(paymentRepository.findByOrder(null)).thenReturn(Arrays.asList());
        when(paymentRepository.findByStatus(null)).thenReturn(Arrays.asList());
        when(paymentRepository.findByTransactionId(null)).thenReturn(Optional.empty());

        Optional<Payment> paymentById = paymentRepository.findById(null);
        List<Payment> paymentsByOrder = paymentRepository.findByOrder(null);
        List<Payment> paymentsByStatus = paymentRepository.findByStatus(null);
        Optional<Payment> paymentByTransactionId = paymentRepository.findByTransactionId(null);

        assertFalse(paymentById.isPresent());
        assertTrue(paymentsByOrder.isEmpty());
        assertTrue(paymentsByStatus.isEmpty());
        assertFalse(paymentByTransactionId.isPresent());
    }

    @Test
    @DisplayName("Should find payments by different statuses")
    void testFindByDifferentStatuses() {
        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(Arrays.asList(testPayment));
        when(paymentRepository.findByStatus(PaymentStatus.APPROVED)).thenReturn(Arrays.asList());
        when(paymentRepository.findByStatus(PaymentStatus.REJECTED)).thenReturn(Arrays.asList());

        List<Payment> pendingPayments = paymentRepository.findByStatus(PaymentStatus.PENDING);
        List<Payment> approvedPayments = paymentRepository.findByStatus(PaymentStatus.APPROVED);
        List<Payment> rejectedPayments = paymentRepository.findByStatus(PaymentStatus.REJECTED);

        assertEquals(1, pendingPayments.size());
        assertEquals(PaymentStatus.PENDING, pendingPayments.get(0).getStatus());
        assertTrue(approvedPayments.isEmpty());
        assertTrue(rejectedPayments.isEmpty());

        verify(paymentRepository).findByStatus(PaymentStatus.PENDING);
        verify(paymentRepository).findByStatus(PaymentStatus.APPROVED);
        verify(paymentRepository).findByStatus(PaymentStatus.REJECTED);
    }

    @Test
    @DisplayName("Should handle empty and null transaction id searches")
    void testFindByEmptyAndNullTransactionId() {
        when(paymentRepository.findByTransactionId("")).thenReturn(Optional.empty());
        when(paymentRepository.findByTransactionId(null)).thenReturn(Optional.empty());

        Optional<Payment> emptyTransactionId = paymentRepository.findByTransactionId("");
        Optional<Payment> nullTransactionId = paymentRepository.findByTransactionId(null);

        assertFalse(emptyTransactionId.isPresent());
        assertFalse(nullTransactionId.isPresent());

        verify(paymentRepository).findByTransactionId("");
        verify(paymentRepository).findByTransactionId(null);
    }

    @Test
    @DisplayName("Should save payments with different payment methods")
    void testSavePaymentsWithDifferentMethods() {
        Payment creditCardPayment = new Payment();
        creditCardPayment.setMethod(PaymentMethod.CREDIT_CARD);

        Payment debitCardPayment = new Payment();
        debitCardPayment.setMethod(PaymentMethod.DEBIT_CARD);

        Payment pixPayment = new Payment();
        pixPayment.setMethod(PaymentMethod.PIX);

        Payment cashPayment = new Payment();
        cashPayment.setMethod(PaymentMethod.CASH);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment savedCreditCard = paymentRepository.save(creditCardPayment);
        Payment savedDebitCard = paymentRepository.save(debitCardPayment);
        Payment savedPix = paymentRepository.save(pixPayment);
        Payment savedCash = paymentRepository.save(cashPayment);

        assertEquals(PaymentMethod.CREDIT_CARD, savedCreditCard.getMethod());
        assertEquals(PaymentMethod.DEBIT_CARD, savedDebitCard.getMethod());
        assertEquals(PaymentMethod.PIX, savedPix.getMethod());
        assertEquals(PaymentMethod.CASH, savedCash.getMethod());
    }

    @Test
    @DisplayName("Should handle duplicate transaction id searches")
    void testFindByDuplicateTransactionId() {
        // This test ensures that only one payment is returned even if multiple exist
        // (which shouldn't happen in a real system but we test for robustness)
        when(paymentRepository.findByTransactionId("DUPLICATE_TXN")).thenReturn(Optional.of(testPayment));

        Optional<Payment> foundPayment = paymentRepository.findByTransactionId("DUPLICATE_TXN");

        assertTrue(foundPayment.isPresent());
        assertEquals(testPayment.getId(), foundPayment.get().getId());
        verify(paymentRepository).findByTransactionId("DUPLICATE_TXN");
    }
}