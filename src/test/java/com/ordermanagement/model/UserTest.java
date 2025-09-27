package com.ordermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Customer customer;
    private Seller seller;
    private DeliveryPerson deliveryPerson;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        seller = new Seller();
        deliveryPerson = new DeliveryPerson();
    }

    @Test
    @DisplayName("Should create users with default constructor")
    void testDefaultConstructor() {
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getName());
        assertNull(customer.getEmail());
        assertNull(customer.getPassword());
        assertNull(customer.getPhone());

        assertNotNull(seller);
        assertNotNull(deliveryPerson);
    }

    @Test
    @DisplayName("Should create users with basic constructors")
    void testBasicConstructors() {
        customer = new Customer("customer@example.com", "password123", "John Customer", "555-1234");
        assertEquals("John Customer", customer.getName());
        assertEquals("customer@example.com", customer.getEmail());
        assertEquals("password123", customer.getPassword());
        assertEquals("555-1234", customer.getPhone());

        seller = new Seller("seller@example.com", "password123", "John Seller", "555-5678", "Business", "12345678000195");
        assertEquals("John Seller", seller.getName());
        assertEquals("seller@example.com", seller.getEmail());

        deliveryPerson = new DeliveryPerson("delivery@example.com", "password123", "John Delivery", "555-9999", "BIKE", "ABC-1234");
        assertEquals("John Delivery", deliveryPerson.getName());
        assertEquals("delivery@example.com", deliveryPerson.getEmail());
    }

    @Test
    @DisplayName("Should set and get all user properties")
    void testSettersAndGetters() {
        Long id = 1L;
        String name = "Jane Smith";
        String email = "jane@example.com";
        String password = "securepass";
        String phone = "555-9999";

        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setPhone(phone);

        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(password, customer.getPassword());
        assertEquals(phone, customer.getPhone());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        customer.setName(null);
        customer.setEmail(null);
        customer.setPassword(null);
        customer.setPhone(null);

        assertNull(customer.getName());
        assertNull(customer.getEmail());
        assertNull(customer.getPassword());
        assertNull(customer.getPhone());
    }
}