package com.ordermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class OrderItemDto {
    private Long id;
    private ProductDto product;
    private int quantity;
    private BigDecimal unitPrice;
    private String observations;
    
    public OrderItemDto() {}
    
    public OrderItemDto(ProductDto product, int quantity, String observations) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.observations = observations;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ProductDto getProduct() { return product; }
    public void setProduct(ProductDto product) { this.product = product; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public BigDecimal getSubtotal() {
        if (unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}