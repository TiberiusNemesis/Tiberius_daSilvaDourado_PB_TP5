package com.ordermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
    }

    @Test
    @DisplayName("Should create address with default constructor")
    void testDefaultConstructor() {
        assertNotNull(address);
        assertNull(address.getId());
        assertNull(address.getStreet());
        assertNull(address.getNumber());
        assertNull(address.getComplement());
        assertNull(address.getNeighborhood());
        assertNull(address.getCity());
        assertNull(address.getState());
        assertNull(address.getZipCode());
    }

    @Test
    @DisplayName("Should create address with parameterized constructor")
    void testParameterizedConstructor() {
        address = new Address("Main Street", "123", "Downtown",
                             "New York", "NY", "10001");

        assertEquals("Main Street", address.getStreet());
        assertEquals("123", address.getNumber());
        assertEquals("Downtown", address.getNeighborhood());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZipCode());
    }

    @Test
    @DisplayName("Should set and get all address fields")
    void testSettersAndGetters() {
        Long id = 1L;
        String street = "Broadway";
        String number = "456";
        String complement = "Suite 100";
        String neighborhood = "Manhattan";
        String city = "New York City";
        String state = "New York";
        String zipCode = "10002";
        address.setId(id);
        address.setStreet(street);
        address.setNumber(number);
        address.setComplement(complement);
        address.setNeighborhood(neighborhood);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);

        assertEquals(id, address.getId());
        assertEquals(street, address.getStreet());
        assertEquals(number, address.getNumber());
        assertEquals(complement, address.getComplement());
        assertEquals(neighborhood, address.getNeighborhood());
        assertEquals(city, address.getCity());
        assertEquals(state, address.getState());
        assertEquals(zipCode, address.getZipCode());
    }

    @Test
    @DisplayName("Should format full address correctly")
    void testGetFullAddress() {
        address.setStreet("Test Street");
        address.setNumber("789");
        address.setNeighborhood("Test District");
        address.setCity("Test City");
        address.setState("TC");
        address.setZipCode("12345");

        String result = address.getFullAddress();

        assertNotNull(result);
        assertTrue(result.contains("Test Street"));
        assertTrue(result.contains("789"));
        assertTrue(result.contains("Test District"));
        assertTrue(result.contains("Test City"));
        assertTrue(result.contains("TC"));
        assertTrue(result.contains("12345"));
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        address.setStreet(null);
        address.setNumber(null);
        address.setComplement(null);

        assertNull(address.getStreet());
        assertNull(address.getNumber());
        assertNull(address.getComplement());

        assertDoesNotThrow(() -> address.getFullAddress());
    }

    @Test
    @DisplayName("Should handle empty strings correctly")
    void testEmptyStrings() {
        address.setStreet("");
        address.setNumber("");
        address.setZipCode("");

        assertEquals("", address.getStreet());
        assertEquals("", address.getNumber());
        assertEquals("", address.getZipCode());
    }

    @Test
    @DisplayName("Should handle default flag correctly")
    void testDefaultFlag() {
        assertFalse(address.isDefault());

        address.setDefault(true);
        assertTrue(address.isDefault());

        address.setDefault(false);
        assertFalse(address.isDefault());
    }
}