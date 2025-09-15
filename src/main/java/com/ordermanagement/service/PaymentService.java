package com.ordermanagement.service;

import com.ordermanagement.model.*;
import com.ordermanagement.enums.PaymentMethod;
import com.ordermanagement.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.List;

public class PaymentService {
    private PaymentRepository paymentRepository;
    private PaymentApiClient paymentApiClient;
    
    public PaymentService(PaymentRepository paymentRepository, 
                         PaymentApiClient paymentApiClient) {
        this.paymentRepository = paymentRepository;
        this.paymentApiClient = paymentApiClient;
    }
    
    public Payment processPayment(Order order) {
        Payment payment = new Payment(order, order.getTotal(), 
                                    order.getPaymentMethod(), order.getPaymentCard());
        payment = paymentRepository.save(payment);
        
        try {
            PaymentResult result = paymentApiClient.processPayment(payment);
            if (result.isSuccess()) {
                payment.approve(result.getTransactionId());
                notifyPaymentSuccess(order);
            } else {
                payment.reject(result.getErrorMessage());
                throw new RuntimeException("Payment failed: " + result.getErrorMessage());
            }
        } catch (Exception e) {
            payment.reject(e.getMessage());
            throw new RuntimeException("Payment processing error: " + e.getMessage());
        }
        
        return paymentRepository.save(payment);
    }
    
    public Payment retryPayment(Long orderId, PaymentMethod newMethod, PaymentCard newCard) {
        Order order = getOrderFromPayment(orderId);
        order.setPaymentMethod(newMethod);
        order.setPaymentCard(newCard);
        
        return processPayment(order);
    }
    
    public List<Payment> getPaymentsByOrder(Order order) {
        return paymentRepository.findByOrder(order);
    }
    
    public void transferToSeller(Order order, BigDecimal amount) {
        try {
            Seller seller = getSeller(order);
            paymentApiClient.transfer(seller.getReceivingCard(), amount);
            seller.addToBalance(amount);
            notifyTransferSuccess(seller, amount);
        } catch (Exception e) {
            throw new RuntimeException("Transfer to seller failed: " + e.getMessage());
        }
    }
    
    public void transferToDeliveryPerson(Order order, BigDecimal amount) {
        try {
            DeliveryPerson deliveryPerson = order.getDeliveryPerson();
            if (deliveryPerson != null) {
                paymentApiClient.transfer(deliveryPerson.getReceivingCard(), amount);
                deliveryPerson.addToBalance(amount);
                notifyTransferSuccess(deliveryPerson, amount);
            }
        } catch (Exception e) {
            throw new RuntimeException("Transfer to delivery person failed: " + e.getMessage());
        }
    }
    
    private Order getOrderFromPayment(Long orderId) {
        return null;
    }
    
    private Seller getSeller(Order order) {
        if (!order.getItems().isEmpty()) {
            return order.getItems().get(0).getProduct().getSeller();
        }
        throw new RuntimeException("No seller found for order");
    }
    
    private void notifyPaymentSuccess(Order order) {
        System.out.println("Payment successful for order: " + order.getId());
    }
    
    private void notifyTransferSuccess(User user, BigDecimal amount) {
        System.out.println("Transfer successful: " + amount + " to " + user.getName());
    }
}