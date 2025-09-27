package com.ordermanagement.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {

    private OrderDto orderDto;
    private ProductDto testProduct;
    private OrderItemDto testOrderItem;
    private AddressDto testAddress;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();

        testProduct = new ProductDto("Test Product", "Test Description", new BigDecimal("15.00"), "SNACKS");
        testProduct.setId(1L);

        testOrderItem = new OrderItemDto(testProduct, 2, "No onions");
        testOrderItem.setId(1L);

        testAddress = new AddressDto("Main St", "123", "Downtown", "City", "ST", "12345");
    }

    @Test
    @DisplayName("Should create order DTO with default constructor")
    void testDefaultConstructor() {
        assertNotNull(orderDto);
        assertNull(orderDto.getId());
        assertNull(orderDto.getCustomerId());
        assertNull(orderDto.getCustomerName());
        assertNull(orderDto.getItems());
        assertNull(orderDto.getStatus());
        assertNull(orderDto.getDeliveryAddress());
        assertNull(orderDto.getDeliveryFee());
        assertNull(orderDto.getDiscountAmount());
        assertNull(orderDto.getCouponCode());
        assertNull(orderDto.getObservations());
        assertNull(orderDto.getPaymentMethod());
        assertNull(orderDto.getDeliveryPersonName());
        assertNull(orderDto.getCreatedAt());
        assertNull(orderDto.getUpdatedAt());
        assertNull(orderDto.getCancellationReason());
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void testSettersAndGetters() {
        Long id = 1L;
        Long customerId = 10L;
        String customerName = "John Doe";
        List<OrderItemDto> items = Arrays.asList(testOrderItem);
        String status = "WAITING";
        BigDecimal deliveryFee = new BigDecimal("5.00");
        BigDecimal discountAmount = new BigDecimal("2.50");
        String couponCode = "DISCOUNT10";
        String observations = "Please deliver quickly";
        String paymentMethod = "CREDIT_CARD";
        String deliveryPersonName = "Jane Delivery";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String cancellationReason = "Customer changed mind";

        orderDto.setId(id);
        orderDto.setCustomerId(customerId);
        orderDto.setCustomerName(customerName);
        orderDto.setItems(items);
        orderDto.setStatus(status);
        orderDto.setDeliveryAddress(testAddress);
        orderDto.setDeliveryFee(deliveryFee);
        orderDto.setDiscountAmount(discountAmount);
        orderDto.setCouponCode(couponCode);
        orderDto.setObservations(observations);
        orderDto.setPaymentMethod(paymentMethod);
        orderDto.setDeliveryPersonName(deliveryPersonName);
        orderDto.setCreatedAt(createdAt);
        orderDto.setUpdatedAt(updatedAt);
        orderDto.setCancellationReason(cancellationReason);

        assertEquals(id, orderDto.getId());
        assertEquals(customerId, orderDto.getCustomerId());
        assertEquals(customerName, orderDto.getCustomerName());
        assertEquals(items, orderDto.getItems());
        assertEquals(status, orderDto.getStatus());
        assertEquals(testAddress, orderDto.getDeliveryAddress());
        assertEquals(deliveryFee, orderDto.getDeliveryFee());
        assertEquals(discountAmount, orderDto.getDiscountAmount());
        assertEquals(couponCode, orderDto.getCouponCode());
        assertEquals(observations, orderDto.getObservations());
        assertEquals(paymentMethod, orderDto.getPaymentMethod());
        assertEquals(deliveryPersonName, orderDto.getDeliveryPersonName());
        assertEquals(createdAt, orderDto.getCreatedAt());
        assertEquals(updatedAt, orderDto.getUpdatedAt());
        assertEquals(cancellationReason, orderDto.getCancellationReason());
    }

    @Test
    @DisplayName("Should calculate subtotal correctly with no items")
    void testGetSubtotalWithNoItems() {
        orderDto.setItems(null);
        assertEquals(BigDecimal.ZERO, orderDto.getSubtotal());

        orderDto.setItems(new ArrayList<>());
        assertEquals(BigDecimal.ZERO, orderDto.getSubtotal());
    }

    @Test
    @DisplayName("Should calculate subtotal correctly with items")
    void testGetSubtotalWithItems() {
        // First item: 2 x 15.00 = 30.00
        OrderItemDto item1 = new OrderItemDto(testProduct, 2, "No onions");
        item1.setUnitPrice(new BigDecimal("15.00"));

        // Second item: 1 x 8.50 = 8.50
        ProductDto product2 = new ProductDto("Product 2", "Description 2", new BigDecimal("8.50"), "BEVERAGES");
        OrderItemDto item2 = new OrderItemDto(product2, 1, "Extra ice");
        item2.setUnitPrice(new BigDecimal("8.50"));

        List<OrderItemDto> items = Arrays.asList(item1, item2);
        orderDto.setItems(items);

        // Expected: 30.00 + 8.50 = 38.50
        BigDecimal expectedSubtotal = new BigDecimal("38.50");
        assertEquals(0, expectedSubtotal.compareTo(orderDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should calculate total correctly with no fees or discounts")
    void testGetTotalWithNoFeesOrDiscounts() {
        OrderItemDto item = new OrderItemDto(testProduct, 1, "");
        item.setUnitPrice(new BigDecimal("20.00"));
        orderDto.setItems(Arrays.asList(item));

        BigDecimal expectedTotal = new BigDecimal("20.00");
        assertEquals(0, expectedTotal.compareTo(orderDto.getTotal()));
    }

    @Test
    @DisplayName("Should calculate total correctly with delivery fee")
    void testGetTotalWithDeliveryFee() {
        OrderItemDto item = new OrderItemDto(testProduct, 1, "");
        item.setUnitPrice(new BigDecimal("20.00"));
        orderDto.setItems(Arrays.asList(item));
        orderDto.setDeliveryFee(new BigDecimal("5.00"));

        BigDecimal expectedTotal = new BigDecimal("25.00");
        assertEquals(0, expectedTotal.compareTo(orderDto.getTotal()));
    }

    @Test
    @DisplayName("Should calculate total correctly with discount")
    void testGetTotalWithDiscount() {
        OrderItemDto item = new OrderItemDto(testProduct, 1, "");
        item.setUnitPrice(new BigDecimal("20.00"));
        orderDto.setItems(Arrays.asList(item));
        orderDto.setDiscountAmount(new BigDecimal("3.00"));

        BigDecimal expectedTotal = new BigDecimal("17.00");
        assertEquals(0, expectedTotal.compareTo(orderDto.getTotal()));
    }

    @Test
    @DisplayName("Should calculate total correctly with all fees and discounts")
    void testGetTotalWithAllFeesAndDiscounts() {
        OrderItemDto item = new OrderItemDto(testProduct, 2, "");
        item.setUnitPrice(new BigDecimal("15.00"));
        orderDto.setItems(Arrays.asList(item));
        orderDto.setDeliveryFee(new BigDecimal("8.00"));
        orderDto.setDiscountAmount(new BigDecimal("5.00"));

        // Subtotal: 2 x 15.00 = 30.00
        // + Delivery: 8.00
        // - Discount: 5.00
        // Total: 33.00
        BigDecimal expectedTotal = new BigDecimal("33.00");
        assertEquals(0, expectedTotal.compareTo(orderDto.getTotal()));
    }

    @Test
    @DisplayName("Should handle null delivery fee and discount in total calculation")
    void testGetTotalWithNullValues() {
        OrderItemDto item = new OrderItemDto(testProduct, 1, "");
        item.setUnitPrice(new BigDecimal("25.00"));
        orderDto.setItems(Arrays.asList(item));
        orderDto.setDeliveryFee(null);
        orderDto.setDiscountAmount(null);

        BigDecimal expectedTotal = new BigDecimal("25.00");
        assertEquals(0, expectedTotal.compareTo(orderDto.getTotal()));
    }

    @Test
    @DisplayName("Should calculate sequential order number correctly")
    void testGetUserOrderNumber() {
        // Create test orders
        OrderDto order1 = new OrderDto();
        order1.setId(1L);
        order1.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));

        OrderDto order2 = new OrderDto();
        order2.setId(2L);
        order2.setCreatedAt(LocalDateTime.of(2023, 1, 2, 10, 0));

        OrderDto order3 = new OrderDto();
        order3.setId(3L);
        order3.setCreatedAt(LocalDateTime.of(2023, 1, 3, 10, 0));

        List<OrderDto> userOrders = Arrays.asList(order3, order1, order2); // Unsorted

        assertEquals(1, OrderDto.getUserOrderNumber(userOrders, 1L));
        assertEquals(2, OrderDto.getUserOrderNumber(userOrders, 2L));
        assertEquals(3, OrderDto.getUserOrderNumber(userOrders, 3L));
    }

    @Test
    @DisplayName("Should return 1 for non-existent order in getUserOrderNumber")
    void testGetUserOrderNumberNonExistent() {
        OrderDto order1 = new OrderDto();
        order1.setId(1L);
        order1.setCreatedAt(LocalDateTime.now());

        List<OrderDto> userOrders = Arrays.asList(order1);

        assertEquals(1, OrderDto.getUserOrderNumber(userOrders, 999L));
    }

    @Test
    @DisplayName("Should handle empty order list in getUserOrderNumber")
    void testGetUserOrderNumberEmptyList() {
        List<OrderDto> emptyOrders = new ArrayList<>();
        assertEquals(1, OrderDto.getUserOrderNumber(emptyOrders, 1L));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        orderDto.setId(null);
        orderDto.setCustomerId(null);
        orderDto.setCustomerName(null);
        orderDto.setItems(null);
        orderDto.setStatus(null);
        orderDto.setDeliveryAddress(null);
        orderDto.setDeliveryFee(null);
        orderDto.setDiscountAmount(null);
        orderDto.setCouponCode(null);
        orderDto.setObservations(null);
        orderDto.setPaymentMethod(null);
        orderDto.setDeliveryPersonName(null);
        orderDto.setCreatedAt(null);
        orderDto.setUpdatedAt(null);
        orderDto.setCancellationReason(null);

        assertNull(orderDto.getId());
        assertNull(orderDto.getCustomerId());
        assertNull(orderDto.getCustomerName());
        assertNull(orderDto.getItems());
        assertNull(orderDto.getStatus());
        assertNull(orderDto.getDeliveryAddress());
        assertNull(orderDto.getDeliveryFee());
        assertNull(orderDto.getDiscountAmount());
        assertNull(orderDto.getCouponCode());
        assertNull(orderDto.getObservations());
        assertNull(orderDto.getPaymentMethod());
        assertNull(orderDto.getDeliveryPersonName());
        assertNull(orderDto.getCreatedAt());
        assertNull(orderDto.getUpdatedAt());
        assertNull(orderDto.getCancellationReason());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        orderDto.setCustomerName("");
        orderDto.setStatus("");
        orderDto.setCouponCode("");
        orderDto.setObservations("");
        orderDto.setPaymentMethod("");
        orderDto.setDeliveryPersonName("");
        orderDto.setCancellationReason("");

        assertEquals("", orderDto.getCustomerName());
        assertEquals("", orderDto.getStatus());
        assertEquals("", orderDto.getCouponCode());
        assertEquals("", orderDto.getObservations());
        assertEquals("", orderDto.getPaymentMethod());
        assertEquals("", orderDto.getDeliveryPersonName());
        assertEquals("", orderDto.getCancellationReason());
    }

    @Test
    @DisplayName("Should handle different order statuses")
    void testOrderStatuses() {
        String[] statuses = {"WAITING", "IN_PREPARATION", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"};

        for (String status : statuses) {
            orderDto.setStatus(status);
            assertEquals(status, orderDto.getStatus());
        }
    }

    @Test
    @DisplayName("Should handle different payment methods")
    void testPaymentMethods() {
        String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "PIX", "CASH"};

        for (String paymentMethod : paymentMethods) {
            orderDto.setPaymentMethod(paymentMethod);
            assertEquals(paymentMethod, orderDto.getPaymentMethod());
        }
    }

    @Test
    @DisplayName("Should handle large monetary values")
    void testLargeMonetaryValues() {
        BigDecimal largeDeliveryFee = new BigDecimal("999999.99");
        BigDecimal largeDiscount = new BigDecimal("888888.88");

        orderDto.setDeliveryFee(largeDeliveryFee);
        orderDto.setDiscountAmount(largeDiscount);

        assertEquals(largeDeliveryFee, orderDto.getDeliveryFee());
        assertEquals(largeDiscount, orderDto.getDiscountAmount());
    }

    @Test
    @DisplayName("Should handle long text values")
    void testLongTextValues() {
        String longCustomerName = "Very ".repeat(50) + "Long Customer Name";
        String longObservations = "This is a very ".repeat(100) + "long observation";
        String longCancellationReason = "Extremely ".repeat(25) + "long cancellation reason";

        orderDto.setCustomerName(longCustomerName);
        orderDto.setObservations(longObservations);
        orderDto.setCancellationReason(longCancellationReason);

        assertEquals(longCustomerName, orderDto.getCustomerName());
        assertEquals(longObservations, orderDto.getObservations());
        assertEquals(longCancellationReason, orderDto.getCancellationReason());
    }

    @Test
    @DisplayName("Should handle zero values in calculations")
    void testZeroValuesInCalculations() {
        OrderItemDto zeroItem = new OrderItemDto(testProduct, 0, "");
        zeroItem.setUnitPrice(BigDecimal.ZERO);

        orderDto.setItems(Arrays.asList(zeroItem));
        orderDto.setDeliveryFee(BigDecimal.ZERO);
        orderDto.setDiscountAmount(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, orderDto.getSubtotal());
        assertEquals(BigDecimal.ZERO, orderDto.getTotal());
    }
}