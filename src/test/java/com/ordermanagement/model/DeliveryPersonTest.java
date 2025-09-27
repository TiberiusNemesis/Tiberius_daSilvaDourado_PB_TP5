package com.ordermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryPersonTest {

    private DeliveryPerson deliveryPerson;

    @BeforeEach
    void setUp() {
        deliveryPerson = new DeliveryPerson();
    }

    @Test
    @DisplayName("Should create delivery person with default constructor")
    void testDefaultConstructor() {
        assertNotNull(deliveryPerson);
        assertNull(deliveryPerson.getId());
        assertNull(deliveryPerson.getName());
        assertNull(deliveryPerson.getEmail());
        assertNull(deliveryPerson.getPhone());
        assertNull(deliveryPerson.getVehicleType());
        assertNull(deliveryPerson.getLicensePlate());
        assertTrue(deliveryPerson.isAvailable());
        assertEquals(BigDecimal.ZERO, deliveryPerson.getBalance());
    }

    @Test
    @DisplayName("Should create delivery person with basic constructor")
    void testBasicConstructor() {
        deliveryPerson = new DeliveryPerson("john@delivery.com", "password",
                                           "John Delivery", "555-1234", "BIKE", "ABC-1234");

        assertEquals("John Delivery", deliveryPerson.getName());
        assertEquals("john@delivery.com", deliveryPerson.getEmail());
        assertEquals("555-1234", deliveryPerson.getPhone());
        assertEquals("BIKE", deliveryPerson.getVehicleType());
        assertEquals("ABC-1234", deliveryPerson.getLicensePlate());
        assertTrue(deliveryPerson.isAvailable());
        assertEquals(BigDecimal.ZERO, deliveryPerson.getBalance());
    }

    @Test
    @DisplayName("Should handle availability status")
    void testAvailabilityStatus() {
        assertTrue(deliveryPerson.isAvailable());

        deliveryPerson.setAvailable(false);
        assertFalse(deliveryPerson.isAvailable());

        deliveryPerson.setAvailable(true);
        assertTrue(deliveryPerson.isAvailable());
    }




    @Test
    @DisplayName("Should handle vehicle information")
    void testVehicleInformation() {
        deliveryPerson.setVehicleType("CAR");
        deliveryPerson.setLicensePlate("ABC-1234");

        assertEquals("CAR", deliveryPerson.getVehicleType());
        assertEquals("ABC-1234", deliveryPerson.getLicensePlate());
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        Long id = 1L;
        String name = "Delivery Guy";
        String email = "delivery@example.com";
        String phone = "555-9999";
        BigDecimal balance = new BigDecimal("100.00");
        PaymentCard receivingCard = new PaymentCard();

        deliveryPerson.setId(id);
        deliveryPerson.setName(name);
        deliveryPerson.setEmail(email);
        deliveryPerson.setPhone(phone);
        deliveryPerson.setBalance(balance);
        deliveryPerson.setReceivingCard(receivingCard);

        assertEquals(id, deliveryPerson.getId());
        assertEquals(name, deliveryPerson.getName());
        assertEquals(email, deliveryPerson.getEmail());
        assertEquals(phone, deliveryPerson.getPhone());
        assertEquals(balance, deliveryPerson.getBalance());
        assertEquals(receivingCard, deliveryPerson.getReceivingCard());
    }


    @Test
    @DisplayName("Should handle balance operations")
    void testBalanceOperations() {
        assertEquals(BigDecimal.ZERO, deliveryPerson.getBalance());

        BigDecimal amount = new BigDecimal("50.00");
        deliveryPerson.addToBalance(amount);
        assertEquals(amount, deliveryPerson.getBalance());

        BigDecimal transferAmount = new BigDecimal("25.00");
        assertTrue(deliveryPerson.requestTransfer(transferAmount));
        assertEquals(new BigDecimal("25.00"), deliveryPerson.getBalance());

        assertFalse(deliveryPerson.requestTransfer(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        deliveryPerson.setName(null);
        deliveryPerson.setEmail(null);
        deliveryPerson.setPhone(null);
        deliveryPerson.setVehicleType(null);

        assertNull(deliveryPerson.getName());
        assertNull(deliveryPerson.getEmail());
        assertNull(deliveryPerson.getPhone());
        assertNull(deliveryPerson.getVehicleType());
    }

}