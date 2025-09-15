package com.ordermanagement.model;

import com.ordermanagement.enums.PaymentMethod;
import com.ordermanagement.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Order order;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentCard card;
    private PaymentStatus status;
    private String transactionId;
    private String failureReason;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    
    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Payment(Order order, BigDecimal amount, PaymentMethod method, PaymentCard card) {
        this();
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.card = card;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    
    public PaymentCard getCard() { return card; }
    public void setCard(PaymentCard card) { this.card = card; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public void approve(String transactionId) {
        this.status = PaymentStatus.APPROVED;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }
    
    public void reject(String reason) {
        this.status = PaymentStatus.REJECTED;
        this.failureReason = reason;
        this.processedAt = LocalDateTime.now();
    }
}