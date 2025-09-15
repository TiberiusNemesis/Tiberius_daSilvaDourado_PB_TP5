package com.ordermanagement.repository;

import com.ordermanagement.model.Payment;
import com.ordermanagement.model.Order;
import com.ordermanagement.enums.PaymentStatus;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
    List<Payment> findByOrder(Order order);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByTransactionId(String transactionId);
    void deleteById(Long id);
}