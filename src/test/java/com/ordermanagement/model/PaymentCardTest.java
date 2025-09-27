package com.ordermanagement.model;

import com.ordermanagement.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PaymentCardTest {

    private PaymentCard paymentCard;

    @BeforeEach
    void setUp() {
        paymentCard = new PaymentCard();
    }

    @Test
    @DisplayName("Should create payment card with default constructor")
    void testDefaultConstructor() {
        assertNotNull(paymentCard);
        assertNull(paymentCard.getId());
        assertNull(paymentCard.getCardNumber());
        assertNull(paymentCard.getHolderName());
        assertNull(paymentCard.getCvv());
        assertNull(paymentCard.getType());
        assertFalse(paymentCard.isDefault());
    }

    @Test
    @DisplayName("Should create payment card with full constructor")
    void testFullConstructor() {
        String number = "4111111111111111";
        String holderName = "JOHN DOE";
        String expiryDate = "12/25";
        String cvv = "123";
        PaymentMethod type = PaymentMethod.CREDIT_CARD;

        paymentCard = new PaymentCard(number, holderName, expiryDate, cvv, type);

        assertEquals(number, paymentCard.getCardNumber());
        assertEquals(holderName, paymentCard.getHolderName());
        assertEquals(expiryDate, paymentCard.getExpiryDate());
        assertEquals(cvv, paymentCard.getCvv());
        assertEquals(type, paymentCard.getType());
    }


    @Test
    @DisplayName("Should mask card number")
    void testMaskCardNumber() {
        paymentCard.setCardNumber("4111111111111111");
        assertEquals("**** **** **** 1111", paymentCard.getMaskedCardNumber());

        paymentCard.setCardNumber("5555555555554444");
        assertEquals("**** **** **** 4444", paymentCard.getMaskedCardNumber());

        paymentCard.setCardNumber("371449635398431");
        assertEquals("**** **** **** 8431", paymentCard.getMaskedCardNumber());
    }



    @Test
    @DisplayName("Should handle default card setting")
    void testDefaultCard() {
        assertFalse(paymentCard.isDefault());

        paymentCard.setDefault(true);
        assertTrue(paymentCard.isDefault());

        paymentCard.setDefault(false);
        assertFalse(paymentCard.isDefault());
    }


    @Test
    @DisplayName("Should set and get all card properties")
    void testSettersAndGetters() {
        Long id = 1L;
        String number = "4532015112830366";
        String holderName = "JANE DOE";
        String expiryDate = "06/26";
        String cvv = "456";
        PaymentMethod type = PaymentMethod.DEBIT_CARD;

        paymentCard.setId(id);
        paymentCard.setCardNumber(number);
        paymentCard.setHolderName(holderName);
        paymentCard.setExpiryDate(expiryDate);
        paymentCard.setCvv(cvv);
        paymentCard.setType(type);

        assertEquals(id, paymentCard.getId());
        assertEquals(number, paymentCard.getCardNumber());
        assertEquals(holderName, paymentCard.getHolderName());
        assertEquals(expiryDate, paymentCard.getExpiryDate());
        assertEquals(cvv, paymentCard.getCvv());
        assertEquals(type, paymentCard.getType());
    }



    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        paymentCard.setCardNumber(null);
        paymentCard.setHolderName(null);
        paymentCard.setCvv(null);

        assertNull(paymentCard.getCardNumber());
        assertNull(paymentCard.getHolderName());
        assertNull(paymentCard.getCvv());
        assertEquals("****", paymentCard.getMaskedCardNumber());
    }
}