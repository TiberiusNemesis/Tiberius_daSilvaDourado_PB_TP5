package com.ordermanagement.model;

import com.ordermanagement.enums.OrderStatus;
import com.ordermanagement.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private Customer customer;
    private Address address;

    @BeforeEach
    void setUp() {
        customer = new Customer("john@example.com", "password", "John Doe", "555-1234");
        address = new Address("Main St", "123", "Downtown", "City", "ST", "12345");
        order = new Order();
    }

    @Test
    @DisplayName("Should create order with default constructor")
    void testDefaultConstructor() {
        assertNotNull(order);
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
        assertEquals(OrderStatus.WAITING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertEquals(BigDecimal.ZERO, order.getDeliveryFee());
        assertEquals(BigDecimal.ZERO, order.getDiscountAmount());
    }

    @Test
    @DisplayName("Should create order with customer and address")
    void testParameterizedConstructor() {
        order = new Order(customer, address);

        assertNotNull(order);
        assertEquals(customer, order.getCustomer());
        assertEquals(address, order.getDeliveryAddress());
        assertEquals(OrderStatus.WAITING, order.getStatus());
        assertNotNull(order.getItems());
        assertEquals(BigDecimal.ZERO, order.getDeliveryFee());
        assertEquals(BigDecimal.ZERO, order.getDiscountAmount());
    }

    @Test
    @DisplayName("Should add items to order")
    void testAddItem() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("10.00"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("20.00"));

        OrderItem item1 = new OrderItem(product1, 2, "No onions");
        OrderItem item2 = new OrderItem(product2, 1, null);

        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().contains(item1));
        assertTrue(order.getItems().contains(item2));
    }


    @Test
    @DisplayName("Should calculate total with items and fees")
    void testCalculateTotal() {
        Product product1 = new Product();
        product1.setPrice(new BigDecimal("10.00"));

        Product product2 = new Product();
        product2.setPrice(new BigDecimal("20.00"));

        order.addItem(new OrderItem(product1, 2, null));
        order.addItem(new OrderItem(product2, 1, null));
        order.setDeliveryFee(new BigDecimal("5.00"));
        order.setDiscountAmount(new BigDecimal("3.00"));

        BigDecimal expectedSubtotal = new BigDecimal("40.00");
        BigDecimal expectedTotal = new BigDecimal("42.00");

        assertEquals(expectedSubtotal, order.getSubtotal());
        assertEquals(expectedTotal, order.getTotal());
    }

    @Test
    @DisplayName("Should handle order status transitions")
    void testOrderStatusTransitions() {
        assertEquals(OrderStatus.WAITING, order.getStatus());

        order.setStatus(OrderStatus.IN_PREPARATION);
        assertEquals(OrderStatus.IN_PREPARATION, order.getStatus());

        order.setStatus(OrderStatus.ON_THE_WAY);
        assertEquals(OrderStatus.ON_THE_WAY, order.getStatus());

        order.setStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    @DisplayName("Should cancel order with reason")
    void testCancelOrder() {
        String cancellationReason = "Customer requested cancellation";

        assertTrue(order.canBeCancelled());
        order.cancel(cancellationReason);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(cancellationReason, order.getCancellationReason());

        // Test that delivered order cannot be cancelled
        Order deliveredOrder = new Order();
        deliveredOrder.setStatus(OrderStatus.DELIVERED);
        assertFalse(deliveredOrder.canBeCancelled());
    }

    @Test
    @DisplayName("Should set payment details")
    void testPaymentDetails() {
        PaymentCard card = new PaymentCard();
        card.setCardNumber("4111111111111111");
        card.setHolderName("John Doe");

        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        order.setPaymentCard(card);

        assertEquals(PaymentMethod.CREDIT_CARD, order.getPaymentMethod());
        assertEquals(card, order.getPaymentCard());
    }

    @Test
    @DisplayName("Should assign delivery person")
    void testAssignDeliveryPerson() {
        DeliveryPerson deliveryPerson = new DeliveryPerson("delivery@example.com", "password", "Delivery Guy", "555-0001", "Bike", "ABC-1234");
        deliveryPerson.setId(1L);

        order.setDeliveryPerson(deliveryPerson);

        assertEquals(deliveryPerson, order.getDeliveryPerson());
    }

    @Test
    @DisplayName("Should apply coupon code")
    void testApplyCoupon() {
        String couponCode = "DISCOUNT10";
        BigDecimal discountAmount = new BigDecimal("10.00");

        order.setCouponCode(couponCode);
        order.setDiscountAmount(discountAmount);

        assertEquals(couponCode, order.getCouponCode());
        assertEquals(discountAmount, order.getDiscountAmount());
    }

    @Test
    @DisplayName("Should set and get all order properties")
    void testSettersAndGetters() {
        Long id = 1L;
        String observations = "Extra napkins please";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        order.setId(id);
        order.setCustomer(customer);
        order.setDeliveryAddress(address);
        order.setObservations(observations);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);

        assertEquals(id, order.getId());
        assertEquals(customer, order.getCustomer());
        assertEquals(address, order.getDeliveryAddress());
        assertEquals(observations, order.getObservations());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle empty order correctly")
    void testEmptyOrder() {
        assertEquals(BigDecimal.ZERO, order.getSubtotal());
        assertEquals(BigDecimal.ZERO, order.getTotal());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        // Test that setting null values doesn't crash
        order.setCustomer(null);
        order.setDeliveryAddress(null);
        order.setObservations(null);

        assertNull(order.getCustomer());
        assertNull(order.getDeliveryAddress());
        assertNull(order.getObservations());

        // getTotal() should still work as it uses BigDecimal.ZERO defaults
        assertDoesNotThrow(() -> order.getTotal());
    }
}