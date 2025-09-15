package com.ordermanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private List<OrderItemDto> items;
    private String status;
    private AddressDto deliveryAddress;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private String couponCode;
    private String observations;
    private String paymentMethod;
    private String deliveryPersonName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cancellationReason;
    
    public OrderDto() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public AddressDto getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(AddressDto deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getDeliveryPersonName() { return deliveryPersonName; }
    public void setDeliveryPersonName(String deliveryPersonName) { this.deliveryPersonName = deliveryPersonName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public BigDecimal getSubtotal() {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                   .map(OrderItemDto::getSubtotal)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotal() {
        BigDecimal subtotal = getSubtotal();
        BigDecimal delivery = deliveryFee != null ? deliveryFee : BigDecimal.ZERO;
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        return subtotal.add(delivery).subtract(discount);
    }

    // Method to calculate sequential order number per customer
    public static int getUserOrderNumber(List<OrderDto> userOrders, Long orderId) {
        // Sort orders by creation date
        List<OrderDto> sortedOrders = userOrders.stream()
            .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
            .collect(java.util.stream.Collectors.toList());

        // Find the position of the current order in the list
        for (int i = 0; i < sortedOrders.size(); i++) {
            if (sortedOrders.get(i).getId().equals(orderId)) {
                return i + 1; // Return 1-based position
            }
        }
        return 1; // Fallback
    }
}