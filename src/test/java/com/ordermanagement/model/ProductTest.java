package com.ordermanagement.model;

import com.ordermanagement.enums.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    @DisplayName("Should create product with default constructor")
    void testDefaultConstructor() {
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getCategory());
        assertNull(product.getImageUrl());
        assertNull(product.getSeller());
        assertFalse(product.isAvailable());
    }

    @Test
    @DisplayName("Should create product with basic constructor")
    void testBasicConstructor() {
        Seller seller = new Seller("seller@example.com", "password", "Test Seller", "555-0001", "Test Business", "12345678000195");
        product = new Product("Pizza Margherita", "Traditional Italian pizza",
                             new BigDecimal("45.00"), ProductCategory.OTHER, seller);

        assertEquals("Pizza Margherita", product.getName());
        assertEquals("Traditional Italian pizza", product.getDescription());
        assertEquals(new BigDecimal("45.00"), product.getPrice());
        assertEquals(ProductCategory.OTHER, product.getCategory());
        assertEquals(seller, product.getSeller());
        assertTrue(product.isAvailable());
    }

    @Test
    @DisplayName("Should set and get all product properties")
    void testSettersAndGetters() {
        Long id = 1L;
        String name = "Burger";
        String description = "Delicious beef burger";
        BigDecimal price = new BigDecimal("25.50");
        ProductCategory category = ProductCategory.OTHER;
        String imageUrl = "http://example.com/burger.jpg";
        boolean available = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Seller seller = new Seller("seller@example.com", "password", "Test Restaurant", "555-0001", "Test Business", "12345678000195");
        seller.setId(1L);

        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setSeller(seller);
        product.setAvailable(available);
        product.setCreatedAt(createdAt);
        product.setUpdatedAt(updatedAt);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertEquals(imageUrl, product.getImageUrl());
        assertEquals(seller, product.getSeller());
        assertEquals(available, product.isAvailable());
        assertEquals(createdAt, product.getCreatedAt());
        assertEquals(updatedAt, product.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle product availability")
    void testProductAvailability() {
        product.setAvailable(true);
        assertTrue(product.isAvailable());

        product.setAvailable(false);
        assertFalse(product.isAvailable());
    }




    @Test
    @DisplayName("Should handle price changes")
    void testPriceChanges() {
        BigDecimal originalPrice = new BigDecimal("20.00");
        product.setPrice(originalPrice);
        assertEquals(originalPrice, product.getPrice());

        BigDecimal newPrice = new BigDecimal("25.00");
        product.setPrice(newPrice);
        assertEquals(newPrice, product.getPrice());

        product.setPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, product.getPrice());
    }

    @Test
    @DisplayName("Should handle different product categories")
    void testProductCategories() {
        product.setCategory(ProductCategory.BEVERAGES);
        assertEquals(ProductCategory.BEVERAGES, product.getCategory());

        product.setCategory(ProductCategory.SNACKS);
        assertEquals(ProductCategory.SNACKS, product.getCategory());

        product.setCategory(ProductCategory.DESSERTS);
        assertEquals(ProductCategory.DESSERTS, product.getCategory());

        product.setCategory(ProductCategory.OTHER);
        assertEquals(ProductCategory.OTHER, product.getCategory());
    }

    @Test
    @DisplayName("Should handle null values correctly")
    void testNullValues() {
        product.setName(null);
        product.setDescription(null);
        product.setPrice(null);
        product.setImageUrl(null);

        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getImageUrl());
    }


}