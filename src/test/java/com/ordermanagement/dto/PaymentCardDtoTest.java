package com.ordermanagement.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCardDtoTest {

    private PaymentCardDto paymentCardDto;

    @BeforeEach
    void setUp() {
        paymentCardDto = new PaymentCardDto();
    }

    @Test
    @DisplayName("Should create payment card DTO with default constructor")
    void testDefaultConstructor() {
        assertNotNull(paymentCardDto);
        assertNull(paymentCardDto.getId());
        assertNull(paymentCardDto.getCardNumber());
        assertNull(paymentCardDto.getHolderName());
        assertNull(paymentCardDto.getExpiryDate());
        assertNull(paymentCardDto.getType());
        assertFalse(paymentCardDto.isDefault()); // Default boolean value
    }

    @Test
    @DisplayName("Should create payment card DTO with parameterized constructor")
    void testParameterizedConstructor() {
        String cardNumber = "4111111111111111";
        String holderName = "John Doe";
        String expiryDate = "12/25";
        String type = "VISA";

        paymentCardDto = new PaymentCardDto(cardNumber, holderName, expiryDate, type);

        assertEquals(cardNumber, paymentCardDto.getCardNumber());
        assertEquals(holderName, paymentCardDto.getHolderName());
        assertEquals(expiryDate, paymentCardDto.getExpiryDate());
        assertEquals(type, paymentCardDto.getType());
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void testSettersAndGetters() {
        Long id = 1L;
        String cardNumber = "5555555555554444";
        String holderName = "Jane Smith";
        String expiryDate = "06/26";
        String type = "MASTERCARD";
        boolean isDefault = true;

        paymentCardDto.setId(id);
        paymentCardDto.setCardNumber(cardNumber);
        paymentCardDto.setHolderName(holderName);
        paymentCardDto.setExpiryDate(expiryDate);
        paymentCardDto.setType(type);
        paymentCardDto.setDefault(isDefault);

        assertEquals(id, paymentCardDto.getId());
        assertEquals(cardNumber, paymentCardDto.getCardNumber());
        assertEquals(holderName, paymentCardDto.getHolderName());
        assertEquals(expiryDate, paymentCardDto.getExpiryDate());
        assertEquals(type, paymentCardDto.getType());
        assertEquals(isDefault, paymentCardDto.isDefault());
    }

    @Test
    @DisplayName("Should generate masked card number correctly")
    void testGetMaskedCardNumber() {
        String cardNumber = "4111111111111111";
        paymentCardDto.setCardNumber(cardNumber);

        String expectedMasked = "**** **** **** 1111";
        assertEquals(expectedMasked, paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should generate masked card number for short card numbers")
    void testGetMaskedCardNumberShort() {
        String shortCardNumber = "1234";
        paymentCardDto.setCardNumber(shortCardNumber);

        String expectedMasked = "**** **** **** 1234";
        assertEquals(expectedMasked, paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle card number shorter than 4 digits")
    void testGetMaskedCardNumberVeryShort() {
        String veryShortCardNumber = "123";
        paymentCardDto.setCardNumber(veryShortCardNumber);

        assertEquals("****", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle null card number in masking")
    void testGetMaskedCardNumberNull() {
        paymentCardDto.setCardNumber(null);
        assertEquals("****", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle empty card number in masking")
    void testGetMaskedCardNumberEmpty() {
        paymentCardDto.setCardNumber("");
        assertEquals("****", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle different card number lengths")
    void testGetMaskedCardNumberDifferentLengths() {
        // Test 15-digit card (American Express)
        paymentCardDto.setCardNumber("371449635398431");
        assertEquals("**** **** **** 8431", paymentCardDto.getMaskedCardNumber());

        // Test 16-digit card (Visa/Mastercard)
        paymentCardDto.setCardNumber("4111111111111111");
        assertEquals("**** **** **** 1111", paymentCardDto.getMaskedCardNumber());

        // Test 19-digit card
        paymentCardDto.setCardNumber("1234567890123456789");
        assertEquals("**** **** **** 6789", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        paymentCardDto.setId(null);
        paymentCardDto.setCardNumber(null);
        paymentCardDto.setHolderName(null);
        paymentCardDto.setExpiryDate(null);
        paymentCardDto.setType(null);

        assertNull(paymentCardDto.getId());
        assertNull(paymentCardDto.getCardNumber());
        assertNull(paymentCardDto.getHolderName());
        assertNull(paymentCardDto.getExpiryDate());
        assertNull(paymentCardDto.getType());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        paymentCardDto.setCardNumber("");
        paymentCardDto.setHolderName("");
        paymentCardDto.setExpiryDate("");
        paymentCardDto.setType("");

        assertEquals("", paymentCardDto.getCardNumber());
        assertEquals("", paymentCardDto.getHolderName());
        assertEquals("", paymentCardDto.getExpiryDate());
        assertEquals("", paymentCardDto.getType());
    }

    @Test
    @DisplayName("Should handle default flag correctly")
    void testDefaultFlag() {
        // Test setting to true
        paymentCardDto.setDefault(true);
        assertTrue(paymentCardDto.isDefault());

        // Test setting to false
        paymentCardDto.setDefault(false);
        assertFalse(paymentCardDto.isDefault());
    }

    @Test
    @DisplayName("Should handle different card types")
    void testCardTypes() {
        String[] cardTypes = {"VISA", "MASTERCARD", "AMERICAN_EXPRESS", "DISCOVER", "ELO", "HIPERCARD"};

        for (String cardType : cardTypes) {
            paymentCardDto.setType(cardType);
            assertEquals(cardType, paymentCardDto.getType());
        }
    }

    @Test
    @DisplayName("Should handle different expiry date formats")
    void testExpiryDateFormats() {
        String[] expiryDates = {
            "12/25",         // MM/YY
            "12/2025",       // MM/YYYY
            "2025-12",       // YYYY-MM
            "Dec 2025",      // Month Year
            "1225"           // MMYY
        };

        for (String expiryDate : expiryDates) {
            paymentCardDto.setExpiryDate(expiryDate);
            assertEquals(expiryDate, paymentCardDto.getExpiryDate());
        }
    }

    @Test
    @DisplayName("Should handle special characters in holder name")
    void testSpecialCharactersInHolderName() {
        String[] holderNames = {
            "José María Santos",
            "O'Connor-Smith",
            "Jean-Pierre François",
            "Mary Anne O'Neil Jr.",
            "François José da Silva"
        };

        for (String holderName : holderNames) {
            paymentCardDto.setHolderName(holderName);
            assertEquals(holderName, paymentCardDto.getHolderName());
        }
    }

    @Test
    @DisplayName("Should handle long holder names")
    void testLongHolderNames() {
        String longHolderName = "Very ".repeat(20) + "Long Holder Name";
        paymentCardDto.setHolderName(longHolderName);
        assertEquals(longHolderName, paymentCardDto.getHolderName());
    }

    @Test
    @DisplayName("Should handle card numbers with spaces")
    void testCardNumbersWithSpaces() {
        String cardNumberWithSpaces = "4111 1111 1111 1111";
        paymentCardDto.setCardNumber(cardNumberWithSpaces);

        assertEquals(cardNumberWithSpaces, paymentCardDto.getCardNumber());
        // The masking should still work correctly
        assertEquals("**** **** **** 1111", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle card numbers with dashes")
    void testCardNumbersWithDashes() {
        String cardNumberWithDashes = "4111-1111-1111-1111";
        paymentCardDto.setCardNumber(cardNumberWithDashes);

        assertEquals(cardNumberWithDashes, paymentCardDto.getCardNumber());
        assertEquals("**** **** **** 1111", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle different valid card numbers")
    void testValidCardNumbers() {
        // Visa
        paymentCardDto.setCardNumber("4111111111111111");
        assertEquals("**** **** **** 1111", paymentCardDto.getMaskedCardNumber());

        // Mastercard
        paymentCardDto.setCardNumber("5555555555554444");
        assertEquals("**** **** **** 4444", paymentCardDto.getMaskedCardNumber());

        // American Express
        paymentCardDto.setCardNumber("378282246310005");
        assertEquals("**** **** **** 0005", paymentCardDto.getMaskedCardNumber());

        // Discover
        paymentCardDto.setCardNumber("6011111111111117");
        assertEquals("**** **** **** 1117", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle Long ID values correctly")
    void testLongIdValues() {
        Long maxLong = Long.MAX_VALUE;
        Long minLong = Long.MIN_VALUE;

        paymentCardDto.setId(maxLong);
        assertEquals(maxLong, paymentCardDto.getId());

        paymentCardDto.setId(minLong);
        assertEquals(minLong, paymentCardDto.getId());

        paymentCardDto.setId(0L);
        assertEquals(0L, paymentCardDto.getId());
    }

    @Test
    @DisplayName("Should handle constructor with null parameters")
    void testConstructorWithNulls() {
        paymentCardDto = new PaymentCardDto(null, null, null, null);

        assertNull(paymentCardDto.getCardNumber());
        assertNull(paymentCardDto.getHolderName());
        assertNull(paymentCardDto.getExpiryDate());
        assertNull(paymentCardDto.getType());
    }

    @Test
    @DisplayName("Should handle masked card number with non-numeric characters")
    void testMaskedCardNumberWithNonNumeric() {
        String cardNumberWithLetters = "4111A111B111C111";
        paymentCardDto.setCardNumber(cardNumberWithLetters);

        // Should still take last 4 characters
        assertEquals("**** **** **** C111", paymentCardDto.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Should handle uppercase and lowercase card types")
    void testCardTypeCaseSensitivity() {
        paymentCardDto.setType("visa");
        assertEquals("visa", paymentCardDto.getType());

        paymentCardDto.setType("VISA");
        assertEquals("VISA", paymentCardDto.getType());

        paymentCardDto.setType("ViSa");
        assertEquals("ViSa", paymentCardDto.getType());
    }

    @Test
    @DisplayName("Should handle very short card numbers in masking edge cases")
    void testMaskedCardNumberEdgeCases() {
        // Empty string
        paymentCardDto.setCardNumber("");
        assertEquals("****", paymentCardDto.getMaskedCardNumber());

        // Single character
        paymentCardDto.setCardNumber("1");
        assertEquals("****", paymentCardDto.getMaskedCardNumber());

        // Two characters
        paymentCardDto.setCardNumber("12");
        assertEquals("****", paymentCardDto.getMaskedCardNumber());

        // Three characters
        paymentCardDto.setCardNumber("123");
        assertEquals("****", paymentCardDto.getMaskedCardNumber());
    }
}