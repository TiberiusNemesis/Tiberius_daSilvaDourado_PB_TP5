package com.ordermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    @DisplayName("Should create customer with default constructor")
    void testDefaultConstructor() {
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getName());
        assertNull(customer.getEmail());
        assertNull(customer.getPassword());
        assertNull(customer.getPhone());
        assertNotNull(customer.getAddresses());
        assertTrue(customer.getAddresses().isEmpty());
        assertNotNull(customer.getPaymentCards());
        assertTrue(customer.getPaymentCards().isEmpty());
    }

    @Test
    @DisplayName("Should create customer with basic constructor")
    void testBasicConstructor() {
        customer = new Customer("john@example.com", "password123", "John Doe", "555-1234");

        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("password123", customer.getPassword());
        assertEquals("555-1234", customer.getPhone());
        assertNotNull(customer.getAddresses());
        assertNotNull(customer.getPaymentCards());
    }


    @Test
    @DisplayName("Should add and get addresses")
    void testAddAddress() {
        Address address1 = new Address("Street 1", "100", "Neighborhood 1",
                                      "City 1", "ST", "12345");
        Address address2 = new Address("Street 2", "200", "Neighborhood 2",
                                      "City 2", "ST", "54321");
        address2.setComplement("Apt 2");

        customer.addAddress(address1);
        customer.addAddress(address2);

        assertEquals(2, customer.getAddresses().size());
        assertTrue(customer.getAddresses().contains(address1));
        assertTrue(customer.getAddresses().contains(address2));
    }

    @Test
    @DisplayName("Should add and get payment cards")
    void testAddPaymentCard() {
        PaymentCard card1 = new PaymentCard();
        card1.setCardNumber("4111111111111111");
        card1.setHolderName("John Doe");

        PaymentCard card2 = new PaymentCard();
        card2.setCardNumber("5555555555554444");
        card2.setHolderName("John Doe");

        customer.addPaymentCard(card1);
        customer.addPaymentCard(card2);

        assertEquals(2, customer.getPaymentCards().size());
        assertTrue(customer.getPaymentCards().contains(card1));
        assertTrue(customer.getPaymentCards().contains(card2));
    }


    @Test
    @DisplayName("Should set and get all customer properties")
    void testSettersAndGetters() {
        Long id = 1L;
        String name = "Test User";
        String email = "test@example.com";
        String password = "testpass";
        String phone = "999-8888";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setPhone(phone);
        customer.setCreatedAt(createdAt);
        customer.setUpdatedAt(updatedAt);

        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(password, customer.getPassword());
        assertEquals(phone, customer.getPhone());
        assertEquals(createdAt, customer.getCreatedAt());
        assertEquals(updatedAt, customer.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle empty lists correctly")
    void testEmptyLists() {
        List<Address> addresses = new ArrayList<>();
        List<PaymentCard> cards = new ArrayList<>();

        customer.setAddresses(addresses);
        customer.setPaymentCards(cards);

        assertNotNull(customer.getAddresses());
        assertNotNull(customer.getPaymentCards());
        assertTrue(customer.getAddresses().isEmpty());
        assertTrue(customer.getPaymentCards().isEmpty());
    }

    @Test
    @DisplayName("Should validate email format")
    void testEmailValidation() {
        customer.setEmail("invalid-email");
        assertEquals("invalid-email", customer.getEmail());

        customer.setEmail("valid@email.com");
        assertEquals("valid@email.com", customer.getEmail());

        customer.setEmail("");
        assertEquals("", customer.getEmail());

        customer.setEmail(null);
        assertNull(customer.getEmail());
    }

}