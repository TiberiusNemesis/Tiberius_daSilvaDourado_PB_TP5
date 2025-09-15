package com.ordermanagement.enums;

public enum OrderStatus {
    WAITING("waiting"),
    IN_PREPARATION("in preparation"),
    ON_THE_WAY("on the way"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}