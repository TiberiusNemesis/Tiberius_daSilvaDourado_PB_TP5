package com.ordermanagement.model;

import com.ordermanagement.enums.ProductCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private String imageUrl;
    private boolean available;
    private Seller seller;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, 
                   ProductCategory category, Seller seller) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.seller = seller;
        this.available = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}