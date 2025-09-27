package com.ordermanagement.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDtoTest {

    private OrderItemDto orderItemDto;
    private ProductDto testProduct;

    @BeforeEach
    void setUp() {
        orderItemDto = new OrderItemDto();
        testProduct = new ProductDto("Test Product", "Test Description", new BigDecimal("15.00"), "SNACKS");
        testProduct.setId(1L);
    }

    @Test
    @DisplayName("Should create order item DTO with default constructor")
    void testDefaultConstructor() {
        assertNotNull(orderItemDto);
        assertNull(orderItemDto.getId());
        assertNull(orderItemDto.getProduct());
        assertEquals(0, orderItemDto.getQuantity()); // Default int value
        assertNull(orderItemDto.getUnitPrice());
        assertNull(orderItemDto.getObservations());
    }

    @Test
    @DisplayName("Should create order item DTO with parameterized constructor")
    void testParameterizedConstructor() {
        String observations = "No onions please";
        int quantity = 2;

        orderItemDto = new OrderItemDto(testProduct, quantity, observations);

        assertEquals(testProduct, orderItemDto.getProduct());
        assertEquals(quantity, orderItemDto.getQuantity());
        assertEquals(testProduct.getPrice(), orderItemDto.getUnitPrice());
        assertEquals(observations, orderItemDto.getObservations());
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void testSettersAndGetters() {
        Long id = 1L;
        int quantity = 3;
        BigDecimal unitPrice = new BigDecimal("12.50");
        String observations = "Extra cheese";

        orderItemDto.setId(id);
        orderItemDto.setProduct(testProduct);
        orderItemDto.setQuantity(quantity);
        orderItemDto.setUnitPrice(unitPrice);
        orderItemDto.setObservations(observations);

        assertEquals(id, orderItemDto.getId());
        assertEquals(testProduct, orderItemDto.getProduct());
        assertEquals(quantity, orderItemDto.getQuantity());
        assertEquals(unitPrice, orderItemDto.getUnitPrice());
        assertEquals(observations, orderItemDto.getObservations());
    }

    @Test
    @DisplayName("Should calculate subtotal correctly")
    void testGetSubtotal() {
        BigDecimal unitPrice = new BigDecimal("15.00");
        int quantity = 3;

        orderItemDto.setUnitPrice(unitPrice);
        orderItemDto.setQuantity(quantity);

        BigDecimal expectedSubtotal = new BigDecimal("45.00");
        assertEquals(0, expectedSubtotal.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should return zero subtotal when unit price is null")
    void testGetSubtotalWithNullUnitPrice() {
        orderItemDto.setUnitPrice(null);
        orderItemDto.setQuantity(5);

        assertEquals(0, BigDecimal.ZERO.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should return zero subtotal when quantity is zero")
    void testGetSubtotalWithZeroQuantity() {
        orderItemDto.setUnitPrice(new BigDecimal("10.00"));
        orderItemDto.setQuantity(0);

        assertEquals(0, BigDecimal.ZERO.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should handle negative quantity in subtotal calculation")
    void testGetSubtotalWithNegativeQuantity() {
        orderItemDto.setUnitPrice(new BigDecimal("10.00"));
        orderItemDto.setQuantity(-2);

        BigDecimal expectedSubtotal = new BigDecimal("-20.00");
        assertEquals(0, expectedSubtotal.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should handle decimal unit prices correctly")
    void testDecimalUnitPrices() {
        BigDecimal decimalPrice = new BigDecimal("12.99");
        orderItemDto.setUnitPrice(decimalPrice);
        orderItemDto.setQuantity(3);

        BigDecimal expectedSubtotal = new BigDecimal("38.97");
        assertEquals(0, expectedSubtotal.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should handle large quantities")
    void testLargeQuantities() {
        orderItemDto.setUnitPrice(new BigDecimal("5.00"));
        orderItemDto.setQuantity(1000);

        BigDecimal expectedSubtotal = new BigDecimal("5000.00");
        assertEquals(0, expectedSubtotal.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should handle very small unit prices")
    void testVerySmallUnitPrices() {
        BigDecimal smallPrice = new BigDecimal("0.01");
        orderItemDto.setUnitPrice(smallPrice);
        orderItemDto.setQuantity(100);

        BigDecimal expectedSubtotal = new BigDecimal("1.00");
        assertEquals(0, expectedSubtotal.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should handle high precision prices")
    void testHighPrecisionPrices() {
        BigDecimal precisePrice = new BigDecimal("12.345");
        orderItemDto.setUnitPrice(precisePrice);
        orderItemDto.setQuantity(2);

        BigDecimal expectedSubtotal = new BigDecimal("24.690");
        assertEquals(0, expectedSubtotal.compareTo(orderItemDto.getSubtotal()));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        orderItemDto.setId(null);
        orderItemDto.setProduct(null);
        orderItemDto.setUnitPrice(null);
        orderItemDto.setObservations(null);

        assertNull(orderItemDto.getId());
        assertNull(orderItemDto.getProduct());
        assertNull(orderItemDto.getUnitPrice());
        assertNull(orderItemDto.getObservations());
    }

    @Test
    @DisplayName("Should handle empty and null observations")
    void testObservations() {
        // Test null observations
        orderItemDto.setObservations(null);
        assertNull(orderItemDto.getObservations());

        // Test empty observations
        orderItemDto.setObservations("");
        assertEquals("", orderItemDto.getObservations());

        // Test normal observations
        String observations = "Please make it spicy";
        orderItemDto.setObservations(observations);
        assertEquals(observations, orderItemDto.getObservations());
    }

    @Test
    @DisplayName("Should handle long observations text")
    void testLongObservations() {
        String longObservations = "Please ".repeat(100) + "make it very special with extra care";
        orderItemDto.setObservations(longObservations);
        assertEquals(longObservations, orderItemDto.getObservations());
    }

    @Test
    @DisplayName("Should handle special characters in observations")
    void testSpecialCharactersInObservations() {
        String specialObservations = "No jalapeños, extra açaí, café com leite";
        orderItemDto.setObservations(specialObservations);
        assertEquals(specialObservations, orderItemDto.getObservations());
    }

    @Test
    @DisplayName("Should handle different quantity values")
    void testQuantityValues() {
        int[] quantities = {0, 1, 10, 100, 999, Integer.MAX_VALUE};

        for (int quantity : quantities) {
            orderItemDto.setQuantity(quantity);
            assertEquals(quantity, orderItemDto.getQuantity());
        }
    }

    @Test
    @DisplayName("Should handle negative quantities")
    void testNegativeQuantities() {
        int[] negativeQuantities = {-1, -10, -100, Integer.MIN_VALUE};

        for (int quantity : negativeQuantities) {
            orderItemDto.setQuantity(quantity);
            assertEquals(quantity, orderItemDto.getQuantity());
        }
    }

    @Test
    @DisplayName("Should preserve product reference correctly")
    void testProductReference() {
        ProductDto product1 = new ProductDto("Product 1", "Description 1", new BigDecimal("10.00"), "SNACKS");
        product1.setId(1L);

        ProductDto product2 = new ProductDto("Product 2", "Description 2", new BigDecimal("20.00"), "BEVERAGES");
        product2.setId(2L);

        orderItemDto.setProduct(product1);
        assertEquals(product1, orderItemDto.getProduct());
        assertEquals(product1.getId(), orderItemDto.getProduct().getId());

        orderItemDto.setProduct(product2);
        assertEquals(product2, orderItemDto.getProduct());
        assertEquals(product2.getId(), orderItemDto.getProduct().getId());
    }

    @Test
    @DisplayName("Should handle constructor with null product")
    void testConstructorWithNullProduct() {
        // Expect a NullPointerException when trying to access product.getPrice()
        assertThrows(NullPointerException.class, () -> {
            new OrderItemDto(null, 2, "Test observations");
        });
    }

    @Test
    @DisplayName("Should copy unit price from product in constructor")
    void testUnitPriceCopyFromProduct() {
        BigDecimal productPrice = new BigDecimal("25.99");
        testProduct.setPrice(productPrice);

        orderItemDto = new OrderItemDto(testProduct, 1, "");

        assertEquals(productPrice, orderItemDto.getUnitPrice());
        assertEquals(0, productPrice.compareTo(orderItemDto.getUnitPrice()));
    }

    @Test
    @DisplayName("Should handle Long ID values correctly")
    void testLongIdValues() {
        Long maxLong = Long.MAX_VALUE;
        Long minLong = Long.MIN_VALUE;

        orderItemDto.setId(maxLong);
        assertEquals(maxLong, orderItemDto.getId());

        orderItemDto.setId(minLong);
        assertEquals(minLong, orderItemDto.getId());

        orderItemDto.setId(0L);
        assertEquals(0L, orderItemDto.getId());
    }
}