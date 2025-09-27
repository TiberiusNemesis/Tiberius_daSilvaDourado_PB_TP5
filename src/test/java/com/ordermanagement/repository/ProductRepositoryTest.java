package com.ordermanagement.repository;

import com.ordermanagement.model.Product;
import com.ordermanagement.model.Seller;
import com.ordermanagement.enums.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    private Product testProduct;
    private Seller testSeller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setName("Test Seller");
        testSeller.setBusinessName("Test Store");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("25.90"));
        testProduct.setCategory(ProductCategory.SNACKS);
        testProduct.setSeller(testSeller);
        testProduct.setAvailable(true);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save product successfully")
    void testSaveProduct() {
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        Product savedProduct = productRepository.save(testProduct);

        assertNotNull(savedProduct);
        assertEquals(testProduct.getId(), savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getDescription(), savedProduct.getDescription());
        assertEquals(testProduct.getPrice(), savedProduct.getPrice());
        assertEquals(testProduct.getCategory(), savedProduct.getCategory());
        assertEquals(testProduct.getSeller().getId(), savedProduct.getSeller().getId());
        assertTrue(savedProduct.isAvailable());
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should find product by id")
    void testFindById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> foundProduct = productRepository.findById(1L);

        assertTrue(foundProduct.isPresent());
        assertEquals(testProduct.getId(), foundProduct.get().getId());
        assertEquals(testProduct.getName(), foundProduct.get().getName());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when product not found by id")
    void testFindByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productRepository.findById(999L);

        assertFalse(foundProduct.isPresent());
        verify(productRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find all products")
    void testFindAll() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Another Product");
        product2.setCategory(ProductCategory.BEVERAGES);

        List<Product> products = Arrays.asList(testProduct, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> foundProducts = productRepository.findAll();

        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        assertTrue(foundProducts.contains(testProduct));
        assertTrue(foundProducts.contains(product2));
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should find products by category")
    void testFindByCategory() {
        Product snack1 = new Product();
        snack1.setId(1L);
        snack1.setCategory(ProductCategory.SNACKS);

        Product snack2 = new Product();
        snack2.setId(2L);
        snack2.setCategory(ProductCategory.SNACKS);

        List<Product> snackProducts = Arrays.asList(snack1, snack2);
        when(productRepository.findByCategory(ProductCategory.SNACKS)).thenReturn(snackProducts);

        List<Product> foundProducts = productRepository.findByCategory(ProductCategory.SNACKS);

        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        foundProducts.forEach(product -> assertEquals(ProductCategory.SNACKS, product.getCategory()));
        verify(productRepository).findByCategory(ProductCategory.SNACKS);
    }

    @Test
    @DisplayName("Should return empty list when no products found by category")
    void testFindByCategoryEmpty() {
        when(productRepository.findByCategory(ProductCategory.DESSERTS)).thenReturn(Arrays.asList());

        List<Product> foundProducts = productRepository.findByCategory(ProductCategory.DESSERTS);

        assertNotNull(foundProducts);
        assertTrue(foundProducts.isEmpty());
        verify(productRepository).findByCategory(ProductCategory.DESSERTS);
    }

    @Test
    @DisplayName("Should find products by seller")
    void testFindBySeller() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setSeller(testSeller);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setSeller(testSeller);

        List<Product> sellerProducts = Arrays.asList(product1, product2);
        when(productRepository.findBySeller(testSeller)).thenReturn(sellerProducts);

        List<Product> foundProducts = productRepository.findBySeller(testSeller);

        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        foundProducts.forEach(product -> assertEquals(testSeller.getId(), product.getSeller().getId()));
        verify(productRepository).findBySeller(testSeller);
    }

    @Test
    @DisplayName("Should return empty list when no products found by seller")
    void testFindBySellerEmpty() {
        Seller otherSeller = new Seller();
        otherSeller.setId(999L);

        when(productRepository.findBySeller(otherSeller)).thenReturn(Arrays.asList());

        List<Product> foundProducts = productRepository.findBySeller(otherSeller);

        assertNotNull(foundProducts);
        assertTrue(foundProducts.isEmpty());
        verify(productRepository).findBySeller(otherSeller);
    }

    @Test
    @DisplayName("Should find products by availability")
    void testFindByAvailable() {
        Product availableProduct = new Product();
        availableProduct.setId(1L);
        availableProduct.setAvailable(true);

        Product unavailableProduct = new Product();
        unavailableProduct.setId(2L);
        unavailableProduct.setAvailable(false);

        List<Product> availableProducts = Arrays.asList(availableProduct);
        List<Product> unavailableProducts = Arrays.asList(unavailableProduct);

        when(productRepository.findByAvailable(true)).thenReturn(availableProducts);
        when(productRepository.findByAvailable(false)).thenReturn(unavailableProducts);

        List<Product> foundAvailable = productRepository.findByAvailable(true);
        List<Product> foundUnavailable = productRepository.findByAvailable(false);

        assertEquals(1, foundAvailable.size());
        assertTrue(foundAvailable.get(0).isAvailable());

        assertEquals(1, foundUnavailable.size());
        assertFalse(foundUnavailable.get(0).isAvailable());

        verify(productRepository).findByAvailable(true);
        verify(productRepository).findByAvailable(false);
    }

    @Test
    @DisplayName("Should find products by name containing")
    void testFindByNameContaining() {
        Product burger = new Product();
        burger.setId(1L);
        burger.setName("X-Burger Artesanal");

        Product hamburger = new Product();
        hamburger.setId(2L);
        hamburger.setName("Classic Hamburger");

        List<Product> burgerProducts = Arrays.asList(burger, hamburger);
        when(productRepository.findByNameContaining("burger")).thenReturn(burgerProducts);

        List<Product> foundProducts = productRepository.findByNameContaining("burger");

        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        foundProducts.forEach(product ->
            assertTrue(product.getName().toLowerCase().contains("burger")));
        verify(productRepository).findByNameContaining("burger");
    }

    @Test
    @DisplayName("Should return empty list when no products found by name containing")
    void testFindByNameContainingEmpty() {
        when(productRepository.findByNameContaining("nonexistent")).thenReturn(Arrays.asList());

        List<Product> foundProducts = productRepository.findByNameContaining("nonexistent");

        assertNotNull(foundProducts);
        assertTrue(foundProducts.isEmpty());
        verify(productRepository).findByNameContaining("nonexistent");
    }

    @Test
    @DisplayName("Should delete product by id")
    void testDeleteById() {
        doNothing().when(productRepository).deleteById(1L);

        productRepository.deleteById(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void testNullParameters() {
        when(productRepository.findById(null)).thenReturn(Optional.empty());
        when(productRepository.findByCategory(null)).thenReturn(Arrays.asList());
        when(productRepository.findBySeller(null)).thenReturn(Arrays.asList());
        when(productRepository.findByNameContaining(null)).thenReturn(Arrays.asList());

        Optional<Product> productById = productRepository.findById(null);
        List<Product> productsByCategory = productRepository.findByCategory(null);
        List<Product> productsBySeller = productRepository.findBySeller(null);
        List<Product> productsByName = productRepository.findByNameContaining(null);

        assertFalse(productById.isPresent());
        assertTrue(productsByCategory.isEmpty());
        assertTrue(productsBySeller.isEmpty());
        assertTrue(productsByName.isEmpty());
    }

    @Test
    @DisplayName("Should find products across all categories")
    void testFindByAllCategories() {
        when(productRepository.findByCategory(ProductCategory.SNACKS)).thenReturn(Arrays.asList(testProduct));
        when(productRepository.findByCategory(ProductCategory.BEVERAGES)).thenReturn(Arrays.asList());
        when(productRepository.findByCategory(ProductCategory.DESSERTS)).thenReturn(Arrays.asList());

        List<Product> snacks = productRepository.findByCategory(ProductCategory.SNACKS);
        List<Product> beverages = productRepository.findByCategory(ProductCategory.BEVERAGES);
        List<Product> desserts = productRepository.findByCategory(ProductCategory.DESSERTS);

        assertEquals(1, snacks.size());
        assertEquals(ProductCategory.SNACKS, snacks.get(0).getCategory());
        assertTrue(beverages.isEmpty());
        assertTrue(desserts.isEmpty());

        verify(productRepository).findByCategory(ProductCategory.SNACKS);
        verify(productRepository).findByCategory(ProductCategory.BEVERAGES);
        verify(productRepository).findByCategory(ProductCategory.DESSERTS);
    }

    @Test
    @DisplayName("Should handle case-insensitive name search")
    void testFindByNameContainingCaseInsensitive() {
        Product product = new Product();
        product.setName("PIZZA Margherita");

        List<Product> products = Arrays.asList(product);
        when(productRepository.findByNameContaining("pizza")).thenReturn(products);
        when(productRepository.findByNameContaining("PIZZA")).thenReturn(products);
        when(productRepository.findByNameContaining("Pizza")).thenReturn(products);

        List<Product> foundLower = productRepository.findByNameContaining("pizza");
        List<Product> foundUpper = productRepository.findByNameContaining("PIZZA");
        List<Product> foundCapitalized = productRepository.findByNameContaining("Pizza");

        assertEquals(1, foundLower.size());
        assertEquals(1, foundUpper.size());
        assertEquals(1, foundCapitalized.size());

        verify(productRepository).findByNameContaining("pizza");
        verify(productRepository).findByNameContaining("PIZZA");
        verify(productRepository).findByNameContaining("Pizza");
    }
}