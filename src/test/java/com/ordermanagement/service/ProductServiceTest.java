package com.ordermanagement.service;

import com.ordermanagement.model.Product;
import com.ordermanagement.model.Seller;
import com.ordermanagement.enums.ProductCategory;
import com.ordermanagement.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;
    private Seller seller1;
    private Seller seller2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository);

        seller1 = new Seller("seller1@example.com", "password", "John Seller", "555-1234", "Restaurant A", "12345678901");
        seller1.setId(1L);

        seller2 = new Seller("seller2@example.com", "password", "Jane Seller", "555-5678", "Restaurant B", "98765432101");
        seller2.setId(2L);

        product1 = new Product("Pizza Margherita", "Classic Italian pizza", new BigDecimal("15.99"), ProductCategory.OTHER, seller1);
        product1.setId(1L);
        product1.setAvailable(true);

        product2 = new Product("Chicken Burger", "Grilled chicken burger", new BigDecimal("12.50"), ProductCategory.OTHER, seller1);
        product2.setId(2L);
        product2.setAvailable(true);

        product3 = new Product("Pasta Carbonara", "Creamy pasta dish", new BigDecimal("13.75"), ProductCategory.OTHER, seller2);
        product3.setId(3L);
        product3.setAvailable(false);
    }

    @Test
    @DisplayName("Should get all available products")
    void testGetAllProducts() {
        List<Product> availableProducts = List.of(product1, product2);
        when(productRepository.findByAvailable(true)).thenReturn(availableProducts);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
        assertFalse(result.contains(product3));
        verify(productRepository, times(1)).findByAvailable(true);
    }

    @Test
    @DisplayName("Should return empty list when no products available")
    void testGetAllProductsEmpty() {
        when(productRepository.findByAvailable(true)).thenReturn(new ArrayList<>());

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findByAvailable(true);
    }

    @Test
    @DisplayName("Should get products by category")
    void testGetProductsByCategory() {
        List<Product> foodProducts = List.of(product1, product2, product3);
        when(productRepository.findByCategory(ProductCategory.OTHER)).thenReturn(foodProducts);

        List<Product> result = productService.getProductsByCategory(ProductCategory.OTHER);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
        assertTrue(result.contains(product3));
        verify(productRepository, times(1)).findByCategory(ProductCategory.OTHER);
    }

    @Test
    @DisplayName("Should return empty list when no products in category")
    void testGetProductsByCategoryEmpty() {
        when(productRepository.findByCategory(ProductCategory.BEVERAGES)).thenReturn(new ArrayList<>());

        List<Product> result = productService.getProductsByCategory(ProductCategory.BEVERAGES);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findByCategory(ProductCategory.BEVERAGES);
    }

    @Test
    @DisplayName("Should get products by seller")
    void testGetProductsBySeller() {
        List<Product> seller1Products = List.of(product1, product2);
        when(productRepository.findBySeller(seller1)).thenReturn(seller1Products);

        List<Product> result = productService.getProductsBySeller(seller1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
        assertFalse(result.contains(product3));
        verify(productRepository, times(1)).findBySeller(seller1);
    }

    @Test
    @DisplayName("Should return empty list when seller has no products")
    void testGetProductsBySellerEmpty() {
        Seller emptySeller = new Seller("empty@example.com", "password", "Empty Seller", "555-0000", "Empty Store", "11111111111");
        when(productRepository.findBySeller(emptySeller)).thenReturn(new ArrayList<>());

        List<Product> result = productService.getProductsBySeller(emptySeller);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findBySeller(emptySeller);
    }

    @Test
    @DisplayName("Should search products by name")
    void testSearchProducts() {
        List<Product> pizzaProducts = List.of(product1);
        when(productRepository.findByNameContaining("Pizza")).thenReturn(pizzaProducts);

        List<Product> result = productService.searchProducts("Pizza");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product1, result.get(0));
        verify(productRepository, times(1)).findByNameContaining("Pizza");
    }

    @Test
    @DisplayName("Should return empty list when no products match search")
    void testSearchProductsNoMatch() {
        when(productRepository.findByNameContaining("Sushi")).thenReturn(new ArrayList<>());

        List<Product> result = productService.searchProducts("Sushi");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findByNameContaining("Sushi");
    }

    @Test
    @DisplayName("Should get product by ID")
    void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(product1, result.get());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional when product not found")
    void testGetProductByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(999L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create product successfully")
    void testCreateProduct() {
        Product newProduct = new Product("New Product", "New Description", new BigDecimal("10.00"), ProductCategory.OTHER, seller1);
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        Product result = productService.createProduct("New Product", "New Description", new BigDecimal("10.00"), ProductCategory.OTHER, seller1);

        assertNotNull(result);
        assertEquals("New Product", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(new BigDecimal("10.00"), result.getPrice());
        assertEquals(ProductCategory.OTHER, result.getCategory());
        assertEquals(seller1, result.getSeller());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(product1)).thenReturn(product1);

        Product result = productService.updateProduct(1L, "Updated Pizza", "Updated description",
                                                     new BigDecimal("18.99"), ProductCategory.OTHER, "new-image.jpg");

        assertNotNull(result);
        assertEquals("Updated Pizza", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(new BigDecimal("18.99"), result.getPrice());
        assertEquals(ProductCategory.OTHER, result.getCategory());
        assertEquals("new-image.jpg", result.getImageUrl());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            productService.updateProduct(999L, "Updated Name", "Updated description",
                                       new BigDecimal("18.99"), ProductCategory.OTHER, "new-image.jpg")
        );
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should toggle product availability from available to unavailable")
    void testToggleProductAvailabilityToUnavailable() {
        product1.setAvailable(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(product1)).thenReturn(product1);

        productService.toggleProductAvailability(1L);

        assertFalse(product1.isAvailable());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    @DisplayName("Should toggle product availability from unavailable to available")
    void testToggleProductAvailabilityToAvailable() {
        product1.setAvailable(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(product1)).thenReturn(product1);

        productService.toggleProductAvailability(1L);

        assertTrue(product1.isAvailable());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    @DisplayName("Should throw exception when toggling availability of non-existent product")
    void testToggleProductAvailabilityNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            productService.toggleProductAvailability(999L)
        );
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any());
    }
}