package com.ordermanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerDto {
    private Long id;
    
    private String email;
    
    private String name;
    
    private String phone;
    private List<AddressDto> addresses;
    private List<PaymentCardDto> paymentCards;
    private LocalDateTime createdAt;
    
    public CustomerDto() {}
    
    public CustomerDto(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public List<AddressDto> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDto> addresses) { this.addresses = addresses; }
    
    public List<PaymentCardDto> getPaymentCards() { return paymentCards; }
    public void setPaymentCards(List<PaymentCardDto> paymentCards) { this.paymentCards = paymentCards; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}