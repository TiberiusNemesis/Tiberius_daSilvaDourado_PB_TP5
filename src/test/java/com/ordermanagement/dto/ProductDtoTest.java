package com.ordermanagement.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductDtoTest {

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
    }

    @Test
    @DisplayName("Should create product DTO with default constructor")
    void testDefaultConstructor() {
        assertNotNull(productDto);
        assertNull(productDto.getId());
        assertNull(productDto.getName());
        assertNull(productDto.getDescription());
        assertNull(productDto.getPrice());
        assertNull(productDto.getCategory());
        assertNull(productDto.getImageUrl());
        assertFalse(productDto.isAvailable()); // Default boolean value
        assertNull(productDto.getSellerName());
        assertNull(productDto.getCreatedAt());
    }

    @Test
    @DisplayName("Should create product DTO with parameterized constructor")
    void testParameterizedConstructor() {
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("25.90");
        String category = "SNACKS";

        LocalDateTime beforeCreation = LocalDateTime.now();
        productDto = new ProductDto(name, description, price, category);
        LocalDateTime afterCreation = LocalDateTime.now();

        assertEquals(name, productDto.getName());
        assertEquals(description, productDto.getDescription());
        assertEquals(price, productDto.getPrice());
        assertEquals(category, productDto.getCategory());
        assertTrue(productDto.isAvailable()); // Default to true in constructor
        assertNotNull(productDto.getCreatedAt());
        assertTrue(productDto.getCreatedAt().isAfter(beforeCreation) || productDto.getCreatedAt().isEqual(beforeCreation));
        assertTrue(productDto.getCreatedAt().isBefore(afterCreation) || productDto.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void testSettersAndGetters() {
        Long id = 1L;
        String name = "X-Burger";
        String description = "Delicious burger";
        BigDecimal price = new BigDecimal("15.50");
        String category = "SNACKS";
        String imageUrl = "https://example.com/burger.jpg";
        boolean available = true;
        String sellerName = "Burger King";
        LocalDateTime createdAt = LocalDateTime.now();

        productDto.setId(id);
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setPrice(price);
        productDto.setCategory(category);
        productDto.setImageUrl(imageUrl);
        productDto.setAvailable(available);
        productDto.setSellerName(sellerName);
        productDto.setCreatedAt(createdAt);

        assertEquals(id, productDto.getId());
        assertEquals(name, productDto.getName());
        assertEquals(description, productDto.getDescription());
        assertEquals(price, productDto.getPrice());
        assertEquals(category, productDto.getCategory());
        assertEquals(imageUrl, productDto.getImageUrl());
        assertEquals(available, productDto.isAvailable());
        assertEquals(sellerName, productDto.getSellerName());
        assertEquals(createdAt, productDto.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        productDto.setId(null);
        productDto.setName(null);
        productDto.setDescription(null);
        productDto.setPrice(null);
        productDto.setCategory(null);
        productDto.setImageUrl(null);
        productDto.setSellerName(null);
        productDto.setCreatedAt(null);

        assertNull(productDto.getId());
        assertNull(productDto.getName());
        assertNull(productDto.getDescription());
        assertNull(productDto.getPrice());
        assertNull(productDto.getCategory());
        assertNull(productDto.getImageUrl());
        assertNull(productDto.getSellerName());
        assertNull(productDto.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle availability flag correctly")
    void testAvailability() {
        // Test setting to true
        productDto.setAvailable(true);
        assertTrue(productDto.isAvailable());

        // Test setting to false
        productDto.setAvailable(false);
        assertFalse(productDto.isAvailable());
    }

    @Test
    @DisplayName("Should handle different price values")
    void testPriceValues() {
        // Test zero price
        productDto.setPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, productDto.getPrice());

        // Test decimal price
        BigDecimal decimalPrice = new BigDecimal("19.99");
        productDto.setPrice(decimalPrice);
        assertEquals(decimalPrice, productDto.getPrice());

        // Test large price
        BigDecimal largePrice = new BigDecimal("999999.99");
        productDto.setPrice(largePrice);
        assertEquals(largePrice, productDto.getPrice());

        // Test price with many decimal places
        BigDecimal precisePrice = new BigDecimal("12.345678");
        productDto.setPrice(precisePrice);
        assertEquals(precisePrice, productDto.getPrice());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        productDto.setName("");
        productDto.setDescription("");
        productDto.setCategory("");
        productDto.setImageUrl("");
        productDto.setSellerName("");

        assertEquals("", productDto.getName());
        assertEquals("", productDto.getDescription());
        assertEquals("", productDto.getCategory());
        assertEquals("", productDto.getImageUrl());
        assertEquals("", productDto.getSellerName());
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void testSpecialCharacters() {
        String specialName = "X-Burgér Spécial ñ";
        String specialDescription = "Delicious burger with açaí & café";
        String specialCategory = "SNACKS & BEVERAGES";
        String specialSellerName = "José's Restaurant & Café";

        productDto.setName(specialName);
        productDto.setDescription(specialDescription);
        productDto.setCategory(specialCategory);
        productDto.setSellerName(specialSellerName);

        assertEquals(specialName, productDto.getName());
        assertEquals(specialDescription, productDto.getDescription());
        assertEquals(specialCategory, productDto.getCategory());
        assertEquals(specialSellerName, productDto.getSellerName());
    }

    @Test
    @DisplayName("Should handle very long text values")
    void testLongTextValues() {
        String longName = "Very ".repeat(50) + "Long Product Name";
        String longDescription = "This is a very ".repeat(100) + "long description";
        String longSellerName = "Extremely ".repeat(25) + "Long Seller Name";

        productDto.setName(longName);
        productDto.setDescription(longDescription);
        productDto.setSellerName(longSellerName);

        assertEquals(longName, productDto.getName());
        assertEquals(longDescription, productDto.getDescription());
        assertEquals(longSellerName, productDto.getSellerName());
    }

    @Test
    @DisplayName("Should handle different category values")
    void testCategoryValues() {
        String[] categories = {"SNACKS", "BEVERAGES", "DESSERTS", "MAIN_COURSES", "APPETIZERS"};

        for (String category : categories) {
            productDto.setCategory(category);
            assertEquals(category, productDto.getCategory());
        }
    }

    @Test
    @DisplayName("Should handle different image URL formats")
    void testImageUrlFormats() {
        String httpUrl = "http://example.com/image.jpg";
        String httpsUrl = "https://example.com/image.png";
        String relativeUrl = "/images/product.gif";
        String dataUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASA...";

        productDto.setImageUrl(httpUrl);
        assertEquals(httpUrl, productDto.getImageUrl());

        productDto.setImageUrl(httpsUrl);
        assertEquals(httpsUrl, productDto.getImageUrl());

        productDto.setImageUrl(relativeUrl);
        assertEquals(relativeUrl, productDto.getImageUrl());

        productDto.setImageUrl(dataUrl);
        assertEquals(dataUrl, productDto.getImageUrl());
    }

    @Test
    @DisplayName("Should handle Long ID values correctly")
    void testLongIdValues() {
        Long maxLong = Long.MAX_VALUE;
        Long minLong = Long.MIN_VALUE;

        productDto.setId(maxLong);
        assertEquals(maxLong, productDto.getId());

        productDto.setId(minLong);
        assertEquals(minLong, productDto.getId());

        productDto.setId(0L);
        assertEquals(0L, productDto.getId());
    }

    @Test
    @DisplayName("Should maintain creation time immutability in constructor")
    void testCreationTimeImmutability() {
        productDto = new ProductDto("Product 1", "Description 1", new BigDecimal("10.00"), "SNACKS");
        LocalDateTime originalCreatedAt = productDto.getCreatedAt();

        // Wait a bit and create another product
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        ProductDto anotherProduct = new ProductDto("Product 2", "Description 2", new BigDecimal("20.00"), "BEVERAGES");

        // Creation times should be different
        assertNotEquals(originalCreatedAt, anotherProduct.getCreatedAt());
        assertTrue(anotherProduct.getCreatedAt().isAfter(originalCreatedAt));
    }

    @Test
    @DisplayName("Should handle BigDecimal price precision")
    void testBigDecimalPrecision() {
        BigDecimal precisePrice = new BigDecimal("12.345");
        productDto.setPrice(precisePrice);

        assertEquals(precisePrice, productDto.getPrice());
        assertEquals(0, precisePrice.compareTo(productDto.getPrice()));

        // Test that scale is preserved
        BigDecimal scaledPrice = new BigDecimal("10.00");
        productDto.setPrice(scaledPrice);
        assertEquals(scaledPrice, productDto.getPrice());
    }

    @Test
    @DisplayName("Should handle constructor with null parameters")
    void testConstructorWithNulls() {
        productDto = new ProductDto(null, null, null, null);

        assertNull(productDto.getName());
        assertNull(productDto.getDescription());
        assertNull(productDto.getPrice());
        assertNull(productDto.getCategory());
        assertTrue(productDto.isAvailable()); // Still defaults to true
        assertNotNull(productDto.getCreatedAt()); // Still sets creation time
    }
}