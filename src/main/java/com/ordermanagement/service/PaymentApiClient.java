package com.ordermanagement.service;

import com.ordermanagement.model.Payment;
import com.ordermanagement.model.PaymentCard;
import com.ordermanagement.model.PaymentResult;
import java.math.BigDecimal;

public interface PaymentApiClient {
    PaymentResult processPayment(Payment payment);
    void transfer(PaymentCard destinationCard, BigDecimal amount);
    boolean validateCard(PaymentCard card);
}