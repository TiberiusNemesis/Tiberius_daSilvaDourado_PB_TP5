package com.ordermanagement.model;

import java.math.BigDecimal;

public class DeliveryPerson extends User {
    private String vehicleType;
    private String licensePlate;
    private BigDecimal balance;
    private PaymentCard receivingCard;
    private boolean available;
    
    public DeliveryPerson() {
        super();
        this.balance = BigDecimal.ZERO;
        this.available = true;
    }
    
    public DeliveryPerson(String email, String password, String name, String phone, 
                         String vehicleType, String licensePlate) {
        super(email, password, name, phone);
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.balance = BigDecimal.ZERO;
        this.available = true;
    }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public PaymentCard getReceivingCard() { return receivingCard; }
    public void setReceivingCard(PaymentCard receivingCard) { this.receivingCard = receivingCard; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
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