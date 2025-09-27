package com.ordermanagement.api;

import com.ordermanagement.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockApiServerTest {

    private MockApiServer mockApiServer;

    @BeforeEach
    void setUp() {
        mockApiServer = new MockApiServer();
    }

    @Test
    @DisplayName("Should initialize with default products")
    void testInitialization() {
        List<ProductDto> products = mockApiServer.getAllProducts();

        assertNotNull(products);
        assertEquals(4, products.size());

        // Check that default products exist
        boolean hasXBurger = products.stream()
            .anyMatch(p -> p.getName().contains("X-Burger"));
        assertTrue(hasXBurger, "Should have X-Burger in default products");

        boolean hasPizza = products.stream()
            .anyMatch(p -> p.getName().contains("Pizza"));
        assertTrue(hasPizza, "Should have Pizza in default products");

        boolean hasSoda = products.stream()
            .anyMatch(p -> p.getName().contains("Cola"));
        assertTrue(hasSoda, "Should have Cola in default products");

        boolean hasDessert = products.stream()
            .anyMatch(p -> p.getName().contains("Pudding"));
        assertTrue(hasDessert, "Should have Pudding in default products");
    }

    @Test
    @DisplayName("Should register customer successfully")
    void testRegisterCustomer() throws MockApiServer.ApiException {
        String email = "test@example.com";
        String password = "password123";
        String name = "Test User";
        String phone = "555-1234";

        CustomerDto customer = mockApiServer.registerCustomer(email, password, name, phone);

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals(email, customer.getEmail());
        assertEquals(name, customer.getName());
        assertEquals(phone, customer.getPhone());
        assertNotNull(customer.getCreatedAt());
    }

    @Test
    @DisplayName("Should fail to register customer with duplicate email")
    void testRegisterCustomerDuplicateEmail() throws MockApiServer.ApiException {
        String email = "duplicate@example.com";
        String password = "password123";
        String name = "Test User";
        String phone = "555-1234";

        // Register first customer
        mockApiServer.registerCustomer(email, password, name, phone);

        // Try to register with same email
        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.registerCustomer(email, password, "Another User", "555-5678")
        );

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    @DisplayName("Should login customer successfully")
    void testLoginCustomer() throws MockApiServer.ApiException {
        String email = "login@example.com";
        String password = "password123";
        String name = "Login User";
        String phone = "555-9999";

        // Register customer first
        CustomerDto registeredCustomer = mockApiServer.registerCustomer(email, password, name, phone);

        // Login
        CustomerDto loggedInCustomer = mockApiServer.loginCustomer(email, password);

        assertNotNull(loggedInCustomer);
        assertEquals(registeredCustomer.getId(), loggedInCustomer.getId());
        assertEquals(email, loggedInCustomer.getEmail());
        assertEquals(name, loggedInCustomer.getName());
    }

    @Test
    @DisplayName("Should fail to login with invalid credentials")
    void testLoginInvalidCredentials() {
        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.loginCustomer("nonexistent@example.com", "wrongpassword")
        );

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    @DisplayName("Should get all products")
    void testGetAllProducts() {
        List<ProductDto> products = mockApiServer.getAllProducts();

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
    void testGetProductsByCategory() {
        List<ProductDto> snacks = mockApiServer.getProductsByCategory("SNACKS");
        List<ProductDto> beverages = mockApiServer.getProductsByCategory("BEVERAGES");
        List<ProductDto> desserts = mockApiServer.getProductsByCategory("DESSERTS");

        assertEquals(2, snacks.size()); // X-Burger and Pizza
        assertEquals(1, beverages.size()); // Cola
        assertEquals(1, desserts.size()); // Pudding

        // Check that categories are correct
        snacks.forEach(product -> assertEquals("SNACKS", product.getCategory()));
        beverages.forEach(product -> assertEquals("BEVERAGES", product.getCategory()));
        desserts.forEach(product -> assertEquals("DESSERTS", product.getCategory()));
    }

    @Test
    @DisplayName("Should get empty list for non-existent category")
    void testGetProductsByNonExistentCategory() {
        List<ProductDto> products = mockApiServer.getProductsByCategory("NONEXISTENT");
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    @DisplayName("Should get product by ID")
    void testGetProductById() throws MockApiServer.ApiException {
        List<ProductDto> allProducts = mockApiServer.getAllProducts();
        ProductDto firstProduct = allProducts.get(0);

        ProductDto foundProduct = mockApiServer.getProductById(firstProduct.getId());

        assertNotNull(foundProduct);
        assertEquals(firstProduct.getId(), foundProduct.getId());
        assertEquals(firstProduct.getName(), foundProduct.getName());
        assertEquals(firstProduct.getCategory(), foundProduct.getCategory());
    }

    @Test
    @DisplayName("Should fail to get product by non-existent ID")
    void testGetProductByNonExistentId() {
        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.getProductById(999L)
        );

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder() throws MockApiServer.ApiException {
        // Register a customer first
        CustomerDto customer = mockApiServer.registerCustomer("order@example.com", "password", "Order User", "555-0000");

        AddressDto deliveryAddress = new AddressDto("Main St", "123", "Downtown", "City", "ST", "12345");

        OrderDto order = mockApiServer.createOrder(customer.getId(), deliveryAddress);

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

        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.createOrder(999L, deliveryAddress)
        );

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should add item to order successfully")
    void testAddItemToOrder() throws MockApiServer.ApiException {
        // Setup customer and order
        CustomerDto customer = mockApiServer.registerCustomer("item@example.com", "password", "Item User", "555-1111");
        AddressDto address = new AddressDto("Test St", "456", "Test Area", "Test City", "TS", "54321");
        OrderDto order = mockApiServer.createOrder(customer.getId(), address);

        // Get a product
        List<ProductDto> products = mockApiServer.getAllProducts();
        ProductDto product = products.get(0);

        // Add item to order
        OrderDto updatedOrder = mockApiServer.addItemToOrder(order.getId(), product.getId(), 2, "Extra spicy");

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
    void testAddItemToNonExistentOrder() {
        List<ProductDto> products = mockApiServer.getAllProducts();
        ProductDto product = products.get(0);

        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.addItemToOrder(999L, product.getId(), 1, "")
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to add non-existent product to order")
    void testAddNonExistentProductToOrder() throws MockApiServer.ApiException {
        // Setup customer and order
        CustomerDto customer = mockApiServer.registerCustomer("baditem@example.com", "password", "Bad Item User", "555-2222");
        AddressDto address = new AddressDto("Bad St", "789", "Bad Area", "Bad City", "BS", "98765");
        OrderDto order = mockApiServer.createOrder(customer.getId(), address);

        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.addItemToOrder(order.getId(), 999L, 1, "")
        );

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to modify order that is not waiting")
    void testAddItemToNonWaitingOrder() throws MockApiServer.ApiException {
        // Setup customer and order
        CustomerDto customer = mockApiServer.registerCustomer("finalized@example.com", "password", "Finalized User", "555-3333");
        AddressDto address = new AddressDto("Final St", "999", "Final Area", "Final City", "FS", "11111");
        OrderDto order = mockApiServer.createOrder(customer.getId(), address);

        // Finalize the order first
        mockApiServer.finalizeOrder(order.getId(), "CREDIT_CARD", new BigDecimal("5.00"));

        // Try to add item to finalized order
        List<ProductDto> products = mockApiServer.getAllProducts();
        ProductDto product = products.get(0);

        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.addItemToOrder(order.getId(), product.getId(), 1, "")
        );

        assertEquals("Cannot modify order that is not waiting", exception.getMessage());
    }

    @Test
    @DisplayName("Should finalize order successfully")
    void testFinalizeOrder() throws MockApiServer.ApiException {
        // Setup customer and order
        CustomerDto customer = mockApiServer.registerCustomer("finalize@example.com", "password", "Finalize User", "555-4444");
        AddressDto address = new AddressDto("Finalize St", "111", "Finalize Area", "Finalize City", "FC", "22222");
        OrderDto order = mockApiServer.createOrder(customer.getId(), address);

        // Finalize order
        OrderDto finalizedOrder = mockApiServer.finalizeOrder(order.getId(), "PIX", new BigDecimal("8.00"));

        assertNotNull(finalizedOrder);
        assertEquals("PIX", finalizedOrder.getPaymentMethod());
        assertEquals("IN_PREPARATION", finalizedOrder.getStatus());
        assertNotNull(finalizedOrder.getDeliveryFee());
        assertTrue(finalizedOrder.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(finalizedOrder.getUpdatedAt().isAfter(finalizedOrder.getCreatedAt()) ||
                  finalizedOrder.getUpdatedAt().equals(finalizedOrder.getCreatedAt()));
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void testCancelOrder() throws MockApiServer.ApiException {
        // Setup customer and order
        CustomerDto customer = mockApiServer.registerCustomer("cancel@example.com", "password", "Cancel User", "555-5555");
        AddressDto address = new AddressDto("Cancel St", "222", "Cancel Area", "Cancel City", "CC", "33333");
        OrderDto order = mockApiServer.createOrder(customer.getId(), address);

        // Cancel order
        String cancellationReason = "Changed my mind";
        OrderDto cancelledOrder = mockApiServer.cancelOrder(order.getId(), cancellationReason);

        assertNotNull(cancelledOrder);
        assertEquals("CANCELLED", cancelledOrder.getStatus());
        assertEquals(cancellationReason, cancelledOrder.getCancellationReason());
        assertTrue(cancelledOrder.getUpdatedAt().isAfter(cancelledOrder.getCreatedAt()) ||
                  cancelledOrder.getUpdatedAt().equals(cancelledOrder.getCreatedAt()));
    }

    @Test
    @DisplayName("Should fail to cancel delivered order")
    void testCancelDeliveredOrder() throws MockApiServer.ApiException {
        // Setup customer and order
        CustomerDto customer = mockApiServer.registerCustomer("delivered@example.com", "password", "Delivered User", "555-6666");
        AddressDto address = new AddressDto("Delivered St", "333", "Delivered Area", "Delivered City", "DC", "44444");
        OrderDto order = mockApiServer.createOrder(customer.getId(), address);

        // Manually set order to delivered (simulating the order lifecycle)
        order.setStatus("DELIVERED");

        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.cancelOrder(order.getId(), "Too late")
        );

        assertTrue(exception.getMessage().contains("Cannot cancel order with status"));
    }

    @Test
    @DisplayName("Should get orders by customer")
    void testGetOrdersByCustomer() throws MockApiServer.ApiException {
        // Setup customer
        CustomerDto customer = mockApiServer.registerCustomer("multiorder@example.com", "password", "Multi Order User", "555-7777");
        AddressDto address = new AddressDto("Multi St", "444", "Multi Area", "Multi City", "MC", "55555");

        // Create multiple orders
        OrderDto order1 = mockApiServer.createOrder(customer.getId(), address);
        OrderDto order2 = mockApiServer.createOrder(customer.getId(), address);

        List<OrderDto> customerOrders = mockApiServer.getOrdersByCustomer(customer.getId());

        assertNotNull(customerOrders);
        assertEquals(2, customerOrders.size());
        assertTrue(customerOrders.stream().anyMatch(o -> o.getId().equals(order1.getId())));
        assertTrue(customerOrders.stream().anyMatch(o -> o.getId().equals(order2.getId())));
    }

    @Test
    @DisplayName("Should get order by ID")
    void testGetOrderById() throws MockApiServer.ApiException {
        // Setup customer and order
        CustomerDto customer = mockApiServer.registerCustomer("getorder@example.com", "password", "Get Order User", "555-8888");
        AddressDto address = new AddressDto("Get St", "555", "Get Area", "Get City", "GC", "66666");
        OrderDto order = mockApiServer.createOrder(customer.getId(), address);

        OrderDto foundOrder = mockApiServer.getOrderById(order.getId());

        assertNotNull(foundOrder);
        assertEquals(order.getId(), foundOrder.getId());
        assertEquals(order.getCustomerId(), foundOrder.getCustomerId());
        assertEquals(order.getStatus(), foundOrder.getStatus());
    }

    @Test
    @DisplayName("Should fail to get non-existent order")
    void testGetNonExistentOrder() {
        MockApiServer.ApiException exception = assertThrows(
            MockApiServer.ApiException.class,
            () -> mockApiServer.getOrderById(999L)
        );

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle case-insensitive category search")
    void testCaseInsensitiveCategorySearch() {
        List<ProductDto> snacksLower = mockApiServer.getProductsByCategory("snacks");
        List<ProductDto> snacksUpper = mockApiServer.getProductsByCategory("SNACKS");
        List<ProductDto> snacksMixed = mockApiServer.getProductsByCategory("Snacks");

        assertEquals(snacksUpper.size(), snacksLower.size());
        assertEquals(snacksUpper.size(), snacksMixed.size());
    }

    @Test
    @DisplayName("Should generate random delivery fees")
    void testRandomDeliveryFees() throws MockApiServer.ApiException {
        CustomerDto customer = mockApiServer.registerCustomer("delivery@example.com", "password", "Delivery User", "555-9999");
        AddressDto address = new AddressDto("Delivery St", "666", "Delivery Area", "Delivery City", "DC", "77777");

        // Create multiple orders and finalize them to check delivery fee randomness
        OrderDto order1 = mockApiServer.createOrder(customer.getId(), address);
        OrderDto order2 = mockApiServer.createOrder(customer.getId(), address);

        OrderDto finalized1 = mockApiServer.finalizeOrder(order1.getId(), "CREDIT_CARD", BigDecimal.ZERO);
        OrderDto finalized2 = mockApiServer.finalizeOrder(order2.getId(), "CREDIT_CARD", BigDecimal.ZERO);

        // Both should have delivery fees between 1.00 and 10.00
        assertTrue(finalized1.getDeliveryFee().compareTo(new BigDecimal("1.00")) >= 0);
        assertTrue(finalized1.getDeliveryFee().compareTo(new BigDecimal("10.00")) <= 0);
        assertTrue(finalized2.getDeliveryFee().compareTo(new BigDecimal("1.00")) >= 0);
        assertTrue(finalized2.getDeliveryFee().compareTo(new BigDecimal("10.00")) <= 0);
    }
}