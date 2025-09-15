package com.ordermanagement.enums;

public enum ProductCategory {
    BEVERAGES("beverages"),
    SNACKS("snacks"),
    DESSERTS("desserts"),
    OTHER("others");
    
    private final String description;
    
    ProductCategory(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}