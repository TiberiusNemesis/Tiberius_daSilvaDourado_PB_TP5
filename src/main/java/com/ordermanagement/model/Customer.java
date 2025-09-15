package com.ordermanagement.model;

import java.util.List;
import java.util.ArrayList;

public class Customer extends User {
    private List<Address> addresses;
    private List<PaymentCard> paymentCards;
    
    public Customer() {
        super();
        this.addresses = new ArrayList<>();
        this.paymentCards = new ArrayList<>();
    }
    
    public Customer(String email, String password, String name, String phone) {
        super(email, password, name, phone);
        this.addresses = new ArrayList<>();
        this.paymentCards = new ArrayList<>();
    }
    
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    
    public void addAddress(Address address) {
        this.addresses.add(address);
    }
    
    public List<PaymentCard> getPaymentCards() { return paymentCards; }
    public void setPaymentCards(List<PaymentCard> paymentCards) { this.paymentCards = paymentCards; }
    
    public void addPaymentCard(PaymentCard card) {
        this.paymentCards.add(card);
    }
}