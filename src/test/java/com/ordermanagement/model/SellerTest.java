package com.ordermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SellerTest {

    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = new Seller();
    }

    @Test
    @DisplayName("Should create seller with default constructor")
    void testDefaultConstructor() {
        assertNotNull(seller);
        assertNull(seller.getId());
        assertNull(seller.getName());
        assertNull(seller.getEmail());
        assertNull(seller.getPhone());
        assertNull(seller.getBusinessName());
        assertNull(seller.getCnpj());
        assertEquals(BigDecimal.ZERO, seller.getBalance());
        assertNull(seller.getReceivingCard());
    }

    @Test
    @DisplayName("Should create seller with basic constructor")
    void testBasicConstructor() {
        seller = new Seller("seller@example.com", "password", "John Seller", "555-1234",
                           "John's Business", "12345678000195");

        assertEquals("John Seller", seller.getName());
        assertEquals("seller@example.com", seller.getEmail());
        assertEquals("password", seller.getPassword());
        assertEquals("555-1234", seller.getPhone());
        assertEquals("John's Business", seller.getBusinessName());
        assertEquals("12345678000195", seller.getCnpj());
        assertEquals(BigDecimal.ZERO, seller.getBalance());
    }

    @Test
    @DisplayName("Should set and get all seller properties")
    void testSettersAndGetters() {
        Long id = 1L;
        String email = "seller@test.com";
        String password = "password123";
        String name = "Test Seller";
        String phone = "555-9999";
        String businessName = "Test Business";
        String cnpj = "12345678000195";
        BigDecimal balance = new BigDecimal("500.00");
        PaymentCard receivingCard = new PaymentCard();

        seller.setId(id);
        seller.setEmail(email);
        seller.setPassword(password);
        seller.setName(name);
        seller.setPhone(phone);
        seller.setBusinessName(businessName);
        seller.setCnpj(cnpj);
        seller.setBalance(balance);
        seller.setReceivingCard(receivingCard);

        assertEquals(id, seller.getId());
        assertEquals(email, seller.getEmail());
        assertEquals(password, seller.getPassword());
        assertEquals(name, seller.getName());
        assertEquals(phone, seller.getPhone());
        assertEquals(businessName, seller.getBusinessName());
        assertEquals(cnpj, seller.getCnpj());
        assertEquals(balance, seller.getBalance());
        assertEquals(receivingCard, seller.getReceivingCard());
    }

    @Test
    @DisplayName("Should handle balance operations")
    void testBalanceOperations() {
        assertEquals(BigDecimal.ZERO, seller.getBalance());

        BigDecimal amount = new BigDecimal("100.00");
        seller.addToBalance(amount);
        assertEquals(amount, seller.getBalance());

        BigDecimal transferAmount = new BigDecimal("50.00");
        assertTrue(seller.requestTransfer(transferAmount));
        assertEquals(new BigDecimal("50.00"), seller.getBalance());

        assertFalse(seller.requestTransfer(new BigDecimal("200.00")));
        assertEquals(new BigDecimal("50.00"), seller.getBalance());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        seller.setName(null);
        seller.setEmail(null);
        seller.setBusinessName(null);
        seller.setCnpj(null);

        assertNull(seller.getName());
        assertNull(seller.getEmail());
        assertNull(seller.getBusinessName());
        assertNull(seller.getCnpj());
    }
}