package com.ordermanagement.model;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Product product;
    private int quantity;
    private BigDecimal unitPrice;
    private String observations;
    
    public OrderItem() {}
    
    public OrderItem(Product product, int quantity, String observations) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.observations = observations;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}