package com.ordermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private OrderItem orderItem;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("25.00"));
        product.setDescription("Test Description");
    }

    @Test
    @DisplayName("Should create order item with default constructor")
    void testDefaultConstructor() {
        orderItem = new OrderItem();

        assertNotNull(orderItem);
        assertNull(orderItem.getId());
        assertNull(orderItem.getProduct());
        assertEquals(0, orderItem.getQuantity());
        assertNull(orderItem.getUnitPrice());
        assertNull(orderItem.getObservations());
    }


    @Test
    @DisplayName("Should create order item with product, quantity and observations")
    void testConstructorWithObservations() {
        String observations = "No onions, extra cheese";
        orderItem = new OrderItem(product, 2, observations);

        assertNotNull(orderItem);
        assertEquals(product, orderItem.getProduct());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(product.getPrice(), orderItem.getUnitPrice());
        assertEquals(observations, orderItem.getObservations());
    }

    @Test
    @DisplayName("Should calculate total price correctly")
    void testCalculateTotalPrice() {
        orderItem = new OrderItem(product, 4, null);

        BigDecimal expectedTotal = new BigDecimal("100.00");
        assertEquals(expectedTotal, orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should handle quantity changes")
    void testQuantityChanges() {
        orderItem = new OrderItem(product, 1, null);
        assertEquals(new BigDecimal("25.00"), orderItem.getSubtotal());

        orderItem.setQuantity(5);
        assertEquals(5, orderItem.getQuantity());
        assertEquals(new BigDecimal("125.00"), orderItem.getSubtotal());

        orderItem.setQuantity(0);
        assertEquals(0, orderItem.getQuantity());
        assertEquals(new BigDecimal("0.00"), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should handle price override")
    void testPriceOverride() {
        orderItem = new OrderItem(product, 2, null);
        assertEquals(new BigDecimal("25.00"), orderItem.getUnitPrice());
        assertEquals(new BigDecimal("50.00"), orderItem.getSubtotal());

        BigDecimal newPrice = new BigDecimal("20.00");
        orderItem.setUnitPrice(newPrice);
        assertEquals(newPrice, orderItem.getUnitPrice());
        assertEquals(new BigDecimal("40.00"), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        Long id = 1L;
        int quantity = 3;
        BigDecimal unitPrice = new BigDecimal("15.50");
        String observations = "Special instructions";

        orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setObservations(observations);

        assertEquals(id, orderItem.getId());
        assertEquals(product, orderItem.getProduct());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(unitPrice, orderItem.getUnitPrice());
        assertEquals(observations, orderItem.getObservations());
    }

    @Test
    @DisplayName("Should handle null product correctly")
    void testNullProduct() {
        // Create OrderItem with default constructor and set null product
        orderItem = new OrderItem();
        orderItem.setProduct(null);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("0.00")); // Set a valid price to avoid NullPointer

        assertNull(orderItem.getProduct());
        assertEquals(new BigDecimal("0.00"), orderItem.getUnitPrice());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("0.00"), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should handle negative quantity")
    void testNegativeQuantity() {
        orderItem = new OrderItem(product, -5, null);

        assertEquals(-5, orderItem.getQuantity());
        assertEquals(new BigDecimal("-125.00"), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should handle zero quantity")
    void testZeroQuantity() {
        orderItem = new OrderItem(product, 0, null);

        assertEquals(0, orderItem.getQuantity());
        assertEquals(new BigDecimal("0.00"), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should handle empty observations")
    void testEmptyObservations() {
        orderItem = new OrderItem(product, 1, "");

        assertEquals("", orderItem.getObservations());

        orderItem.setObservations(null);
        assertNull(orderItem.getObservations());
    }

}