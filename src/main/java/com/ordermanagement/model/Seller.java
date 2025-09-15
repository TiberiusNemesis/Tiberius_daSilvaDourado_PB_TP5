package com.ordermanagement.model;

import java.math.BigDecimal;

public class Seller extends User {
    private String businessName;
    private String cnpj;
    private BigDecimal balance;
    private PaymentCard receivingCard;
    
    public Seller() {
        super();
        this.balance = BigDecimal.ZERO;
    }
    
    public Seller(String email, String password, String name, String phone, 
                  String businessName, String cnpj) {
        super(email, password, name, phone);
        this.businessName = businessName;
        this.cnpj = cnpj;
        this.balance = BigDecimal.ZERO;
    }
    
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public PaymentCard getReceivingCard() { return receivingCard; }
    public void setReceivingCard(PaymentCard receivingCard) { this.receivingCard = receivingCard; }
    
    public void addToBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    
    public boolean requestTransfer(BigDecimal amount) {
        if (this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        return false;
    }
}