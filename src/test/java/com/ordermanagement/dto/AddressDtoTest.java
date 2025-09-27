package com.ordermanagement.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class AddressDtoTest {

    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        addressDto = new AddressDto();
    }

    @Test
    @DisplayName("Should create address DTO with default constructor")
    void testDefaultConstructor() {
        assertNotNull(addressDto);
        assertNull(addressDto.getId());
        assertNull(addressDto.getStreet());
        assertNull(addressDto.getNumber());
        assertNull(addressDto.getNeighborhood());
        assertNull(addressDto.getCity());
        assertNull(addressDto.getState());
        assertNull(addressDto.getZipCode());
        assertNull(addressDto.getComplement());
        assertFalse(addressDto.isDefault()); // Default boolean value
    }

    @Test
    @DisplayName("Should create address DTO with parameterized constructor")
    void testParameterizedConstructor() {
        String street = "Main Street";
        String number = "123";
        String neighborhood = "Downtown";
        String city = "Springfield";
        String state = "IL";
        String zipCode = "62701";

        addressDto = new AddressDto(street, number, neighborhood, city, state, zipCode);

        assertEquals(street, addressDto.getStreet());
        assertEquals(number, addressDto.getNumber());
        assertEquals(neighborhood, addressDto.getNeighborhood());
        assertEquals(city, addressDto.getCity());
        assertEquals(state, addressDto.getState());
        assertEquals(zipCode, addressDto.getZipCode());
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void testSettersAndGetters() {
        Long id = 1L;
        String street = "Oak Avenue";
        String number = "456";
        String neighborhood = "Residential District";
        String city = "Metropolis";
        String state = "NY";
        String zipCode = "10001";
        String complement = "Apartment 2B";
        boolean isDefault = true;

        addressDto.setId(id);
        addressDto.setStreet(street);
        addressDto.setNumber(number);
        addressDto.setNeighborhood(neighborhood);
        addressDto.setCity(city);
        addressDto.setState(state);
        addressDto.setZipCode(zipCode);
        addressDto.setComplement(complement);
        addressDto.setDefault(isDefault);

        assertEquals(id, addressDto.getId());
        assertEquals(street, addressDto.getStreet());
        assertEquals(number, addressDto.getNumber());
        assertEquals(neighborhood, addressDto.getNeighborhood());
        assertEquals(city, addressDto.getCity());
        assertEquals(state, addressDto.getState());
        assertEquals(zipCode, addressDto.getZipCode());
        assertEquals(complement, addressDto.getComplement());
        assertEquals(isDefault, addressDto.isDefault());
    }

    @Test
    @DisplayName("Should generate full address correctly")
    void testGetFullAddress() {
        addressDto.setStreet("Main Street");
        addressDto.setNumber("123");
        addressDto.setNeighborhood("Downtown");
        addressDto.setCity("Springfield");
        addressDto.setState("IL");
        addressDto.setZipCode("62701");

        String expectedFullAddress = "Main Street, 123 - Downtown, Springfield - IL 62701";
        assertEquals(expectedFullAddress, addressDto.getFullAddress());
    }

    @Test
    @DisplayName("Should handle null values in full address generation")
    void testGetFullAddressWithNulls() {
        // Test with all null values
        addressDto.setStreet(null);
        addressDto.setNumber(null);
        addressDto.setNeighborhood(null);
        addressDto.setCity(null);
        addressDto.setState(null);
        addressDto.setZipCode(null);

        String fullAddress = addressDto.getFullAddress();
        assertNotNull(fullAddress);
        assertEquals("null, null - null, null - null null", fullAddress);
    }

    @Test
    @DisplayName("Should handle empty strings in full address generation")
    void testGetFullAddressWithEmptyStrings() {
        addressDto.setStreet("");
        addressDto.setNumber("");
        addressDto.setNeighborhood("");
        addressDto.setCity("");
        addressDto.setState("");
        addressDto.setZipCode("");

        String expectedFullAddress = ",  - ,  -  ";
        assertEquals(expectedFullAddress, addressDto.getFullAddress());
    }

    @Test
    @DisplayName("Should handle partial address data in full address generation")
    void testGetFullAddressWithPartialData() {
        addressDto.setStreet("Elm Street");
        addressDto.setNumber("789");
        addressDto.setNeighborhood(null);
        addressDto.setCity("Springfield");
        addressDto.setState("IL");
        addressDto.setZipCode("");

        String expectedFullAddress = "Elm Street, 789 - null, Springfield - IL ";
        assertEquals(expectedFullAddress, addressDto.getFullAddress());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        addressDto.setId(null);
        addressDto.setStreet(null);
        addressDto.setNumber(null);
        addressDto.setNeighborhood(null);
        addressDto.setCity(null);
        addressDto.setState(null);
        addressDto.setZipCode(null);
        addressDto.setComplement(null);

        assertNull(addressDto.getId());
        assertNull(addressDto.getStreet());
        assertNull(addressDto.getNumber());
        assertNull(addressDto.getNeighborhood());
        assertNull(addressDto.getCity());
        assertNull(addressDto.getState());
        assertNull(addressDto.getZipCode());
        assertNull(addressDto.getComplement());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        addressDto.setStreet("");
        addressDto.setNumber("");
        addressDto.setNeighborhood("");
        addressDto.setCity("");
        addressDto.setState("");
        addressDto.setZipCode("");
        addressDto.setComplement("");

        assertEquals("", addressDto.getStreet());
        assertEquals("", addressDto.getNumber());
        assertEquals("", addressDto.getNeighborhood());
        assertEquals("", addressDto.getCity());
        assertEquals("", addressDto.getState());
        assertEquals("", addressDto.getZipCode());
        assertEquals("", addressDto.getComplement());
    }

    @Test
    @DisplayName("Should handle default flag correctly")
    void testDefaultFlag() {
        // Test setting to true
        addressDto.setDefault(true);
        assertTrue(addressDto.isDefault());

        // Test setting to false
        addressDto.setDefault(false);
        assertFalse(addressDto.isDefault());
    }

    @Test
    @DisplayName("Should handle special characters in address fields")
    void testSpecialCharacters() {
        String streetWithSpecialChars = "Rua José María O'Connor";
        String neighborhoodWithSpecialChars = "São José do Rio Preto";
        String cityWithSpecialChars = "São Paulo";
        String complementWithSpecialChars = "Bloco A, Apto 123 - Próximo ao açougue";

        addressDto.setStreet(streetWithSpecialChars);
        addressDto.setNeighborhood(neighborhoodWithSpecialChars);
        addressDto.setCity(cityWithSpecialChars);
        addressDto.setComplement(complementWithSpecialChars);

        assertEquals(streetWithSpecialChars, addressDto.getStreet());
        assertEquals(neighborhoodWithSpecialChars, addressDto.getNeighborhood());
        assertEquals(cityWithSpecialChars, addressDto.getCity());
        assertEquals(complementWithSpecialChars, addressDto.getComplement());
    }

    @Test
    @DisplayName("Should handle long text values")
    void testLongTextValues() {
        String longStreet = "Very ".repeat(50) + "Long Street Name";
        String longNeighborhood = "Extremely ".repeat(25) + "Long Neighborhood Name";
        String longCity = "Super ".repeat(30) + "Long City Name";
        String longComplement = "This is a very ".repeat(100) + "long complement description";

        addressDto.setStreet(longStreet);
        addressDto.setNeighborhood(longNeighborhood);
        addressDto.setCity(longCity);
        addressDto.setComplement(longComplement);

        assertEquals(longStreet, addressDto.getStreet());
        assertEquals(longNeighborhood, addressDto.getNeighborhood());
        assertEquals(longCity, addressDto.getCity());
        assertEquals(longComplement, addressDto.getComplement());
    }

    @Test
    @DisplayName("Should handle different zip code formats")
    void testZipCodeFormats() {
        String[] zipCodes = {
            "12345-678",     // Brazilian format
            "12345",         // US format
            "12345-6789",    // US extended format
            "K1A 0A6",       // Canadian format
            "SW1A 1AA",      // UK format
            "00000"          // All zeros
        };

        for (String zipCode : zipCodes) {
            addressDto.setZipCode(zipCode);
            assertEquals(zipCode, addressDto.getZipCode());
        }
    }

    @Test
    @DisplayName("Should handle different number formats")
    void testNumberFormats() {
        String[] numbers = {
            "123",           // Simple number
            "123A",          // Number with letter
            "123-125",       // Range
            "123/125",       // Range with slash
            "Lot 5",         // Lot number
            "S/N",           // Without number (Sem Número)
            "KM 15"          // Kilometer marker
        };

        for (String number : numbers) {
            addressDto.setNumber(number);
            assertEquals(number, addressDto.getNumber());
        }
    }

    @Test
    @DisplayName("Should handle different state formats")
    void testStateFormats() {
        String[] states = {
            "SP",            // Brazilian state abbreviation
            "São Paulo",     // Full state name
            "CA",            // US state abbreviation
            "California",    // Full US state name
            "ON",            // Canadian province
            "Ontario"        // Full Canadian province name
        };

        for (String state : states) {
            addressDto.setState(state);
            assertEquals(state, addressDto.getState());
        }
    }

    @Test
    @DisplayName("Should handle Long ID values correctly")
    void testLongIdValues() {
        Long maxLong = Long.MAX_VALUE;
        Long minLong = Long.MIN_VALUE;

        addressDto.setId(maxLong);
        assertEquals(maxLong, addressDto.getId());

        addressDto.setId(minLong);
        assertEquals(minLong, addressDto.getId());

        addressDto.setId(0L);
        assertEquals(0L, addressDto.getId());
    }

    @Test
    @DisplayName("Should handle constructor with null parameters")
    void testConstructorWithNulls() {
        addressDto = new AddressDto(null, null, null, null, null, null);

        assertNull(addressDto.getStreet());
        assertNull(addressDto.getNumber());
        assertNull(addressDto.getNeighborhood());
        assertNull(addressDto.getCity());
        assertNull(addressDto.getState());
        assertNull(addressDto.getZipCode());
    }

    @Test
    @DisplayName("Should maintain immutability of full address format")
    void testFullAddressFormat() {
        // Ensure full address format is consistent
        addressDto.setStreet("First St");
        addressDto.setNumber("100");
        addressDto.setNeighborhood("Center");
        addressDto.setCity("Testville");
        addressDto.setState("TS");
        addressDto.setZipCode("00000");

        String fullAddress1 = addressDto.getFullAddress();
        String fullAddress2 = addressDto.getFullAddress();

        assertEquals(fullAddress1, fullAddress2);
        assertEquals("First St, 100 - Center, Testville - TS 00000", fullAddress1);
    }
}