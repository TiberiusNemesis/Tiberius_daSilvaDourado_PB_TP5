package com.ordermanagement.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDtoTest {

    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
    }

    @Test
    @DisplayName("Should create customer DTO with default constructor")
    void testDefaultConstructor() {
        assertNotNull(customerDto);
        assertNull(customerDto.getId());
        assertNull(customerDto.getEmail());
        assertNull(customerDto.getName());
        assertNull(customerDto.getPhone());
        assertNull(customerDto.getAddresses());
        assertNull(customerDto.getPaymentCards());
        assertNull(customerDto.getCreatedAt());
    }

    @Test
    @DisplayName("Should create customer DTO with parameterized constructor")
    void testParameterizedConstructor() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        customerDto = new CustomerDto("john@example.com", "John Doe", "555-1234");
        LocalDateTime afterCreation = LocalDateTime.now();

        assertEquals("john@example.com", customerDto.getEmail());
        assertEquals("John Doe", customerDto.getName());
        assertEquals("555-1234", customerDto.getPhone());
        assertNotNull(customerDto.getCreatedAt());
        assertTrue(customerDto.getCreatedAt().isAfter(beforeCreation) || customerDto.getCreatedAt().isEqual(beforeCreation));
        assertTrue(customerDto.getCreatedAt().isBefore(afterCreation) || customerDto.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void testSettersAndGetters() {
        Long id = 1L;
        String email = "test@example.com";
        String name = "Test User";
        String phone = "999-8888";
        LocalDateTime createdAt = LocalDateTime.now();

        List<AddressDto> addresses = new ArrayList<>();
        AddressDto address = new AddressDto("Main St", "123", "Downtown", "City", "ST", "12345");
        addresses.add(address);

        List<PaymentCardDto> paymentCards = new ArrayList<>();
        PaymentCardDto card = new PaymentCardDto("4111111111111111", "John Doe", "12/25", "VISA");
        paymentCards.add(card);

        customerDto.setId(id);
        customerDto.setEmail(email);
        customerDto.setName(name);
        customerDto.setPhone(phone);
        customerDto.setAddresses(addresses);
        customerDto.setPaymentCards(paymentCards);
        customerDto.setCreatedAt(createdAt);

        assertEquals(id, customerDto.getId());
        assertEquals(email, customerDto.getEmail());
        assertEquals(name, customerDto.getName());
        assertEquals(phone, customerDto.getPhone());
        assertEquals(addresses, customerDto.getAddresses());
        assertEquals(paymentCards, customerDto.getPaymentCards());
        assertEquals(createdAt, customerDto.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        customerDto.setId(null);
        customerDto.setEmail(null);
        customerDto.setName(null);
        customerDto.setPhone(null);
        customerDto.setAddresses(null);
        customerDto.setPaymentCards(null);
        customerDto.setCreatedAt(null);

        assertNull(customerDto.getId());
        assertNull(customerDto.getEmail());
        assertNull(customerDto.getName());
        assertNull(customerDto.getPhone());
        assertNull(customerDto.getAddresses());
        assertNull(customerDto.getPaymentCards());
        assertNull(customerDto.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle empty collections")
    void testEmptyCollections() {
        List<AddressDto> emptyAddresses = new ArrayList<>();
        List<PaymentCardDto> emptyCards = new ArrayList<>();

        customerDto.setAddresses(emptyAddresses);
        customerDto.setPaymentCards(emptyCards);

        assertNotNull(customerDto.getAddresses());
        assertNotNull(customerDto.getPaymentCards());
        assertTrue(customerDto.getAddresses().isEmpty());
        assertTrue(customerDto.getPaymentCards().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple addresses")
    void testMultipleAddresses() {
        AddressDto homeAddress = new AddressDto("Home St", "123", "Residential", "Home City", "HC", "11111");
        AddressDto workAddress = new AddressDto("Work Ave", "456", "Business", "Work City", "WC", "22222");

        List<AddressDto> addresses = Arrays.asList(homeAddress, workAddress);
        customerDto.setAddresses(addresses);

        assertEquals(2, customerDto.getAddresses().size());
        assertTrue(customerDto.getAddresses().contains(homeAddress));
        assertTrue(customerDto.getAddresses().contains(workAddress));
    }

    @Test
    @DisplayName("Should handle multiple payment cards")
    void testMultiplePaymentCards() {
        PaymentCardDto visa = new PaymentCardDto("4111111111111111", "John Doe", "12/25", "VISA");
        PaymentCardDto mastercard = new PaymentCardDto("5555555555554444", "John Doe", "06/26", "MASTERCARD");

        List<PaymentCardDto> cards = Arrays.asList(visa, mastercard);
        customerDto.setPaymentCards(cards);

        assertEquals(2, customerDto.getPaymentCards().size());
        assertTrue(customerDto.getPaymentCards().contains(visa));
        assertTrue(customerDto.getPaymentCards().contains(mastercard));
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        customerDto.setEmail("");
        customerDto.setName("");
        customerDto.setPhone("");

        assertEquals("", customerDto.getEmail());
        assertEquals("", customerDto.getName());
        assertEquals("", customerDto.getPhone());
    }

    @Test
    @DisplayName("Should handle special characters in fields")
    void testSpecialCharacters() {
        String specialEmail = "user+test@example.com";
        String specialName = "José María O'Connor";
        String specialPhone = "+1 (555) 123-4567";

        customerDto.setEmail(specialEmail);
        customerDto.setName(specialName);
        customerDto.setPhone(specialPhone);

        assertEquals(specialEmail, customerDto.getEmail());
        assertEquals(specialName, customerDto.getName());
        assertEquals(specialPhone, customerDto.getPhone());
    }

    @Test
    @DisplayName("Should maintain immutability of creation time in constructor")
    void testCreationTimeImmutability() {
        customerDto = new CustomerDto("test@example.com", "Test User", "555-0000");
        LocalDateTime originalCreatedAt = customerDto.getCreatedAt();

        // Try to modify the createdAt (simulating some time passing)
        try {
            Thread.sleep(10); // Wait a bit
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Create another customer with same constructor
        CustomerDto anotherCustomer = new CustomerDto("another@example.com", "Another User", "555-9999");

        // The creation times should be different
        assertNotEquals(originalCreatedAt, anotherCustomer.getCreatedAt());
        assertTrue(anotherCustomer.getCreatedAt().isAfter(originalCreatedAt));
    }

    @Test
    @DisplayName("Should handle long values correctly")
    void testLongValues() {
        Long maxLong = Long.MAX_VALUE;
        Long minLong = Long.MIN_VALUE;

        customerDto.setId(maxLong);
        assertEquals(maxLong, customerDto.getId());

        customerDto.setId(minLong);
        assertEquals(minLong, customerDto.getId());

        customerDto.setId(0L);
        assertEquals(0L, customerDto.getId());
    }

    @Test
    @DisplayName("Should handle very long string values")
    void testVeryLongStringValues() {
        String longEmail = "a".repeat(100) + "@example.com";
        String longName = "Very ".repeat(50) + "Long Name";
        String longPhone = "1234567890".repeat(10);

        customerDto.setEmail(longEmail);
        customerDto.setName(longName);
        customerDto.setPhone(longPhone);

        assertEquals(longEmail, customerDto.getEmail());
        assertEquals(longName, customerDto.getName());
        assertEquals(longPhone, customerDto.getPhone());
    }
}