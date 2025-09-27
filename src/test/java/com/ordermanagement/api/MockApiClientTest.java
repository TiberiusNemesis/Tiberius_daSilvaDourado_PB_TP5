package com.ordermanagement.api;

import com.ordermanagement.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockApiClientTest {

    private MockApiClient mockApiClient;

    @BeforeEach
    void setUp() {
        mockApiClient = new MockApiClient();
    }

    @Test
    @DisplayName("Should register customer successfully")
    void testRegisterCustomer() throws IOException {
        String email = "test@example.com";
        String password = "password123";
        String name = "Test User";
        String phone = "555-1234";

        CustomerDto customer = mockApiClient.registerCustomer(email, password, name, phone);

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals(email, customer.getEmail());
        assertEquals(name, customer.getName());
        assertEquals(phone, customer.getPhone());
        assertNotNull(customer.getCreatedAt());
    }

    @Test
    @DisplayName("Should fail to register customer with duplicate email")
    void testRegisterCustomerDuplicateEmail() throws IOException {
        String email = "duplicate@example.com";
        String password = "password123";
        String name = "Test User";
        String phone = "555-1234";

        // Register first customer
        mockApiClient.registerCustomer(email, password, name, phone);

        // Try to register with same email
        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.registerCustomer(email, password, "Another User", "555-5678")
        );

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    @DisplayName("Should login customer successfully")
    void testLoginCustomer() throws IOException {
        String email = "login@example.com";
        String password = "password123";
        String name = "Login User";
        String phone = "555-9999";

        // Register customer first
        CustomerDto registeredCustomer = mockApiClient.registerCustomer(email, password, name, phone);

        // Login
        CustomerDto loggedInCustomer = mockApiClient.loginCustomer(email, password);

        assertNotNull(loggedInCustomer);
        assertEquals(registeredCustomer.getId(), loggedInCustomer.getId());
        assertEquals(email, loggedInCustomer.getEmail());
        assertEquals(name, loggedInCustomer.getName());
    }

    @Test
    @DisplayName("Should fail to login with invalid credentials")
    void testLoginInvalidCredentials() {
        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.loginCustomer("nonexistent@example.com", "wrongpassword")
        );

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    @DisplayName("Should get all products")
    void testGetAllProducts() throws IOException {
        List<ProductDto> products = mockApiClient.getAllProducts();

        assertNotNull(products);
        assertEquals(4, products.size());

        // Verify product details
        ProductDto xBurger = products.stream()
            .filter(p -> p.getName().contains("X-Burger"))
            .findFirst()
            .orElse(null);

        assertNotNull(xBurger);
        assertEquals("SNACKS", xBurger.getCategory());
        assertTrue(xBurger.getPrice().compareTo(BigDecimal.ZERO) > 0);
        assertEquals("Maria's Snack Bar", xBurger.getSellerName());
    }

    @Test
    @DisplayName("Should get products by category")
    void testGetProductsByCategory() throws IOException {
        List<ProductDto> snacks = mockApiClient.getProductsByCategory("SNACKS");
        List<ProductDto> beverages = mockApiClient.getProductsByCategory("BEVERAGES");
        List<ProductDto> desserts = mockApiClient.getProductsByCategory("DESSERTS");

        assertEquals(2, snacks.size()); // X-Burger and Pizza
        assertEquals(1, beverages.size()); // Cola
        assertEquals(1, desserts.size()); // Pudding

        // Check that categories are correct
        snacks.forEach(product -> assertEquals("SNACKS", product.getCategory()));
        beverages.forEach(product -> assertEquals("BEVERAGES", product.getCategory()));
        desserts.forEach(product -> assertEquals("DESSERTS", product.getCategory()));
    }

    @Test
    @DisplayName("Should get product by ID")
    void testGetProductById() throws IOException {
        List<ProductDto> allProducts = mockApiClient.getAllProducts();
        ProductDto firstProduct = allProducts.get(0);

        ProductDto foundProduct = mockApiClient.getProductById(firstProduct.getId());

        assertNotNull(foundProduct);
        assertEquals(firstProduct.getId(), foundProduct.getId());
        assertEquals(firstProduct.getName(), foundProduct.getName());
        assertEquals(firstProduct.getCategory(), foundProduct.getCategory());
    }

    @Test
    @DisplayName("Should fail to get product by non-existent ID")
    void testGetProductByNonExistentId() {
        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.getProductById(999L)
        );

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder() throws IOException {
        // Register a customer first
        CustomerDto customer = mockApiClient.registerCustomer("order@example.com", "password", "Order User", "555-0000");

        AddressDto deliveryAddress = new AddressDto("Main St", "123", "Downtown", "City", "ST", "12345");

        OrderDto order = mockApiClient.createOrder(customer.getId(), deliveryAddress);

        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(customer.getId(), order.getCustomerId());
        assertEquals(customer.getName(), order.getCustomerName());
        assertEquals("WAITING", order.getStatus());
        assertEquals(deliveryAddress, order.getDeliveryAddress());
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    @DisplayName("Should fail to create order for non-existent customer")
    void testCreateOrderNonExistentCustomer() {
        AddressDto deliveryAddress = new AddressDto("Main St", "123", "Downtown", "City", "ST", "12345");

        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.createOrder(999L, deliveryAddress)
        );

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should add item to order successfully")
    void testAddItemToOrder() throws IOException {
        // Setup customer and order
        CustomerDto customer = mockApiClient.registerCustomer("item@example.com", "password", "Item User", "555-1111");
        AddressDto address = new AddressDto("Test St", "456", "Test Area", "Test City", "TS", "54321");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        // Get a product
        List<ProductDto> products = mockApiClient.getAllProducts();
        ProductDto product = products.get(0);

        // Add item to order
        OrderDto updatedOrder = mockApiClient.addItemToOrder(order.getId(), product.getId(), 2, "Extra spicy");

        assertNotNull(updatedOrder);
        assertEquals(order.getId(), updatedOrder.getId());
        assertEquals(1, updatedOrder.getItems().size());

        OrderItemDto addedItem = updatedOrder.getItems().get(0);
        assertEquals(product.getId(), addedItem.getProduct().getId());
        assertEquals(2, addedItem.getQuantity());
        assertEquals("Extra spicy", addedItem.getObservations());
        assertEquals(product.getPrice(), addedItem.getUnitPrice());
    }

    @Test
    @DisplayName("Should fail to add item to non-existent order")
    void testAddItemToNonExistentOrder() throws IOException {
        List<ProductDto> products = mockApiClient.getAllProducts();
        ProductDto product = products.get(0);

        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.addItemToOrder(999L, product.getId(), 1, "")
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to add non-existent product to order")
    void testAddNonExistentProductToOrder() throws IOException {
        // Setup customer and order
        CustomerDto customer = mockApiClient.registerCustomer("baditem@example.com", "password", "Bad Item User", "555-2222");
        AddressDto address = new AddressDto("Bad St", "789", "Bad Area", "Bad City", "BS", "98765");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.addItemToOrder(order.getId(), 999L, 1, "")
        );

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should finalize order successfully")
    void testFinalizeOrder() throws IOException {
        // Setup customer and order
        CustomerDto customer = mockApiClient.registerCustomer("finalize@example.com", "password", "Finalize User", "555-4444");
        AddressDto address = new AddressDto("Finalize St", "111", "Finalize Area", "Finalize City", "FC", "22222");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        // Finalize order
        OrderDto finalizedOrder = mockApiClient.finalizeOrder(order.getId(), "PIX", new BigDecimal("8.00"));

        assertNotNull(finalizedOrder);
        assertEquals("PIX", finalizedOrder.getPaymentMethod());
        assertEquals("IN_PREPARATION", finalizedOrder.getStatus());
        assertNotNull(finalizedOrder.getDeliveryFee());
        assertTrue(finalizedOrder.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(finalizedOrder.getUpdatedAt().isAfter(finalizedOrder.getCreatedAt()) ||
                  finalizedOrder.getUpdatedAt().equals(finalizedOrder.getCreatedAt()));
    }

    @Test
    @DisplayName("Should fail to finalize non-existent order")
    void testFinalizeNonExistentOrder() {
        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.finalizeOrder(999L, "CREDIT_CARD", new BigDecimal("5.00"))
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void testCancelOrder() throws IOException {
        // Setup customer and order
        CustomerDto customer = mockApiClient.registerCustomer("cancel@example.com", "password", "Cancel User", "555-5555");
        AddressDto address = new AddressDto("Cancel St", "222", "Cancel Area", "Cancel City", "CC", "33333");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        // Cancel order
        String cancellationReason = "Changed my mind";
        OrderDto cancelledOrder = mockApiClient.cancelOrder(order.getId(), cancellationReason);

        assertNotNull(cancelledOrder);
        assertEquals("CANCELLED", cancelledOrder.getStatus());
        assertEquals(cancellationReason, cancelledOrder.getCancellationReason());
        assertTrue(cancelledOrder.getUpdatedAt().isAfter(cancelledOrder.getCreatedAt()) ||
                  cancelledOrder.getUpdatedAt().equals(cancelledOrder.getCreatedAt()));
    }

    @Test
    @DisplayName("Should fail to cancel non-existent order")
    void testCancelNonExistentOrder() {
        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.cancelOrder(999L, "Reason")
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get orders by customer")
    void testGetOrdersByCustomer() throws IOException {
        // Setup customer
        CustomerDto customer = mockApiClient.registerCustomer("multiorder@example.com", "password", "Multi Order User", "555-7777");
        AddressDto address = new AddressDto("Multi St", "444", "Multi Area", "Multi City", "MC", "55555");

        // Create multiple orders
        OrderDto order1 = mockApiClient.createOrder(customer.getId(), address);
        OrderDto order2 = mockApiClient.createOrder(customer.getId(), address);

        List<OrderDto> customerOrders = mockApiClient.getOrdersByCustomer(customer.getId());

        assertNotNull(customerOrders);
        assertEquals(2, customerOrders.size());
        assertTrue(customerOrders.stream().anyMatch(o -> o.getId().equals(order1.getId())));
        assertTrue(customerOrders.stream().anyMatch(o -> o.getId().equals(order2.getId())));
    }

    @Test
    @DisplayName("Should fail to get orders for non-existent customer")
    void testGetOrdersByNonExistentCustomer() {
        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.getOrdersByCustomer(999L)
        );

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should get order by ID")
    void testGetOrderById() throws IOException {
        // Setup customer and order
        CustomerDto customer = mockApiClient.registerCustomer("getorder@example.com", "password", "Get Order User", "555-8888");
        AddressDto address = new AddressDto("Get St", "555", "Get Area", "Get City", "GC", "66666");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        OrderDto foundOrder = mockApiClient.getOrderById(order.getId());

        assertNotNull(foundOrder);
        assertEquals(order.getId(), foundOrder.getId());
        assertEquals(order.getCustomerId(), foundOrder.getCustomerId());
        assertEquals(order.getStatus(), foundOrder.getStatus());
    }

    @Test
    @DisplayName("Should fail to get non-existent order")
    void testGetNonExistentOrder() {
        IOException exception = assertThrows(
            IOException.class,
            () -> mockApiClient.getOrderById(999L)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should close without errors")
    void testClose() throws IOException {
        // Should not throw any exception
        assertDoesNotThrow(() -> mockApiClient.close());
    }

    @Test
    @DisplayName("Should handle multiple operations in sequence")
    void testMultipleOperationsSequence() throws IOException {
        // Register customer
        CustomerDto customer = mockApiClient.registerCustomer("sequence@example.com", "password", "Sequence User", "555-9000");

        // Get products
        List<ProductDto> products = mockApiClient.getAllProducts();
        assertTrue(products.size() > 0);

        // Create order
        AddressDto address = new AddressDto("Sequence St", "777", "Sequence Area", "Sequence City", "SC", "88888");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        // Add multiple items
        ProductDto product1 = products.get(0);
        ProductDto product2 = products.get(1);

        OrderDto orderWithItem1 = mockApiClient.addItemToOrder(order.getId(), product1.getId(), 1, "First item");
        OrderDto orderWithItems = mockApiClient.addItemToOrder(orderWithItem1.getId(), product2.getId(), 2, "Second item");

        assertEquals(2, orderWithItems.getItems().size());

        // Finalize order
        OrderDto finalizedOrder = mockApiClient.finalizeOrder(orderWithItems.getId(), "DEBIT_CARD", new BigDecimal("6.00"));
        assertEquals("IN_PREPARATION", finalizedOrder.getStatus());

        // Get customer orders
        List<OrderDto> customerOrders = mockApiClient.getOrdersByCustomer(customer.getId());
        assertEquals(1, customerOrders.size());
        assertEquals(finalizedOrder.getId(), customerOrders.get(0).getId());
    }

    @Test
    @DisplayName("Should handle empty observations correctly")
    void testEmptyObservations() throws IOException {
        // Setup customer and order
        CustomerDto customer = mockApiClient.registerCustomer("empty@example.com", "password", "Empty User", "555-9100");
        AddressDto address = new AddressDto("Empty St", "888", "Empty Area", "Empty City", "EC", "99999");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        // Get a product
        List<ProductDto> products = mockApiClient.getAllProducts();
        ProductDto product = products.get(0);

        // Add item with empty observations
        OrderDto updatedOrder = mockApiClient.addItemToOrder(order.getId(), product.getId(), 1, "");

        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());

        OrderItemDto addedItem = updatedOrder.getItems().get(0);
        assertEquals("", addedItem.getObservations());
    }

    @Test
    @DisplayName("Should handle null observations correctly")
    void testNullObservations() throws IOException {
        // Setup customer and order
        CustomerDto customer = mockApiClient.registerCustomer("null@example.com", "password", "Null User", "555-9200");
        AddressDto address = new AddressDto("Null St", "999", "Null Area", "Null City", "NC", "00000");
        OrderDto order = mockApiClient.createOrder(customer.getId(), address);

        // Get a product
        List<ProductDto> products = mockApiClient.getAllProducts();
        ProductDto product = products.get(0);

        // Add item with null observations
        OrderDto updatedOrder = mockApiClient.addItemToOrder(order.getId(), product.getId(), 1, null);

        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());

        OrderItemDto addedItem = updatedOrder.getItems().get(0);
        assertNull(addedItem.getObservations());
    }
}