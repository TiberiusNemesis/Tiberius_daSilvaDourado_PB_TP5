package com.ordermanagement.model;

import com.ordermanagement.enums.PaymentMethod;

public class PaymentCard {
    private Long id;
    private String cardNumber;
    private String holderName;
    private String expiryDate;
    private String cvv;
    private PaymentMethod type;
    private boolean isDefault;
    
    public PaymentCard() {}
    
    public PaymentCard(String cardNumber, String holderName, String expiryDate, 
                      String cvv, PaymentMethod type) {
        this.cardNumber = cardNumber;
        this.holderName = holderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.type = type;
        this.isDefault = false;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public PaymentMethod getType() { return type; }
    public void setType(PaymentMethod type) { this.type = type; }
    
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    
    public String getMaskedCardNumber() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }
        return "****";
    }
}