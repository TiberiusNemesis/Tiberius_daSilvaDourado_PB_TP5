package com.ordermanagement.model;

import com.ordermanagement.enums.OrderStatus;
import com.ordermanagement.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Order {
    private Long id;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private Address deliveryAddress;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private String couponCode;
    private String observations;
    private PaymentMethod paymentMethod;
    private PaymentCard paymentCard;
    private DeliveryPerson deliveryPerson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cancellationReason;
    
    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.WAITING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deliveryFee = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
    }
    
    public Order(Customer customer, Address deliveryAddress) {
        this();
        this.customer = customer;
        this.deliveryAddress = deliveryAddress;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public void addItem(OrderItem item) {
        this.items.add(item);
    }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public Address getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(Address deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentCard getPaymentCard() { return paymentCard; }
    public void setPaymentCard(PaymentCard paymentCard) { this.paymentCard = paymentCard; }
    
    public DeliveryPerson getDeliveryPerson() { return deliveryPerson; }
    public void setDeliveryPerson(DeliveryPerson deliveryPerson) { this.deliveryPerson = deliveryPerson; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public BigDecimal getSubtotal() {
        return items.stream()
                   .map(OrderItem::getSubtotal)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotal() {
        return getSubtotal()
               .add(deliveryFee)
               .subtract(discountAmount);
    }
    
    public boolean canBeCancelled() {
        return status == OrderStatus.WAITING || status == OrderStatus.IN_PREPARATION;
    }
    
    public void cancel(String reason) {
        if (canBeCancelled()) {
            this.status = OrderStatus.CANCELLED;
            this.cancellationReason = reason;
            this.updatedAt = LocalDateTime.now();
        }
    }
}