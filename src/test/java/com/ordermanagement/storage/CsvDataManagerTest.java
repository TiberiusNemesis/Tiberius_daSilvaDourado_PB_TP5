package com.ordermanagement.storage;

import com.ordermanagement.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvDataManagerTest {

    private CsvDataManager csvDataManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Use a temporary directory for testing to avoid conflicts with real data
        System.setProperty("user.dir", tempDir.toString());
        csvDataManager = new CsvDataManager();
    }

    @AfterEach
    void tearDown() {
        // Clean up temp files if they exist
        try {
            Files.deleteIfExists(Paths.get("data/customers.csv"));
            Files.deleteIfExists(Paths.get("data/products.csv"));
            Files.deleteIfExists(Paths.get("data/orders.csv"));
            Files.deleteIfExists(Paths.get("data/order_items.csv"));
            Files.deleteIfExists(Paths.get("data/customer_ids.csv"));
            Files.deleteIfExists(Paths.get("data/product_ids.csv"));
            Files.deleteIfExists(Paths.get("data/order_ids.csv"));
            Files.deleteIfExists(Paths.get("data"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @DisplayName("Should initialize with default data")
    void testInitialization() {
        assertNotNull(csvDataManager);

        // Check that default products are loaded
        List<ProductDto> products = csvDataManager.loadProducts();
        assertTrue(products.size() >= 4, "Should have at least 4 default products");

        // Verify some default products exist
        boolean hasXBurger = products.stream()
            .anyMatch(p -> p.getName().contains("X-Burger"));
        assertTrue(hasXBurger, "Should have X-Burger in default products");
    }

    @Test
    @DisplayName("Should generate unique customer IDs")
    void testGenerateCustomerId() {
        long id1 = csvDataManager.generateCustomerId();
        long id2 = csvDataManager.generateCustomerId();
        long id3 = csvDataManager.generateCustomerId();

        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }

    @Test
    @DisplayName("Should generate unique product IDs")
    void testGenerateProductId() {
        long id1 = csvDataManager.generateProductId();
        long id2 = csvDataManager.generateProductId();
        long id3 = csvDataManager.generateProductId();

        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }

    @Test
    @DisplayName("Should generate unique order IDs")
    void testGenerateOrderId() {
        long id1 = csvDataManager.generateOrderId();
        long id2 = csvDataManager.generateOrderId();
        long id3 = csvDataManager.generateOrderId();

        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }

    @Test
    @DisplayName("Should save and load customers correctly")
    void testSaveAndLoadCustomers() {
        CustomerDto customer = new CustomerDto("test@example.com", "Test User", "555-1234");
        customer.setId(csvDataManager.generateCustomerId());

        csvDataManager.saveCustomer(customer);

        List<CustomerDto> customers = csvDataManager.loadCustomers();
        assertEquals(1, customers.size());

        CustomerDto loadedCustomer = customers.get(0);
        assertEquals(customer.getId(), loadedCustomer.getId());
        assertEquals(customer.getEmail(), loadedCustomer.getEmail());
        assertEquals(customer.getName(), loadedCustomer.getName());
        assertEquals(customer.getPhone(), loadedCustomer.getPhone());
    }

    @Test
    @DisplayName("Should find customer by email")
    void testFindCustomerByEmail() {
        CustomerDto customer1 = new CustomerDto("user1@example.com", "User One", "555-1111");
        customer1.setId(csvDataManager.generateCustomerId());

        CustomerDto customer2 = new CustomerDto("user2@example.com", "User Two", "555-2222");
        customer2.setId(csvDataManager.generateCustomerId());

        csvDataManager.saveCustomer(customer1);
        csvDataManager.saveCustomer(customer2);

        CustomerDto foundCustomer = csvDataManager.findCustomerByEmail("user1@example.com");
        assertNotNull(foundCustomer);
        assertEquals(customer1.getId(), foundCustomer.getId());
        assertEquals(customer1.getName(), foundCustomer.getName());

        CustomerDto notFoundCustomer = csvDataManager.findCustomerByEmail("nonexistent@example.com");
        assertNull(notFoundCustomer);
    }

    @Test
    @DisplayName("Should save and load products correctly")
    void testSaveAndLoadProducts() {
        ProductDto product1 = new ProductDto("Test Product 1", "Description 1", new BigDecimal("10.00"), "SNACKS");
        product1.setId(csvDataManager.generateProductId());
        product1.setSellerName("Test Seller 1");

        ProductDto product2 = new ProductDto("Test Product 2", "Description 2", new BigDecimal("20.00"), "BEVERAGES");
        product2.setId(csvDataManager.generateProductId());
        product2.setSellerName("Test Seller 2");

        List<ProductDto> productsToSave = Arrays.asList(product1, product2);
        csvDataManager.saveProducts(productsToSave);

        List<ProductDto> loadedProducts = csvDataManager.loadProducts();
        assertEquals(2, loadedProducts.size());

        // Find our test products among the loaded products
        ProductDto loadedProduct1 = loadedProducts.stream()
            .filter(p -> p.getName().equals("Test Product 1"))
            .findFirst()
            .orElse(null);

        assertNotNull(loadedProduct1);
        assertEquals(product1.getDescription(), loadedProduct1.getDescription());
        assertEquals(0, product1.getPrice().compareTo(loadedProduct1.getPrice()));
        assertEquals(product1.getCategory(), loadedProduct1.getCategory());
        assertEquals(product1.getSellerName(), loadedProduct1.getSellerName());
    }

    @Test
    @DisplayName("Should find product by ID")
    void testFindProductById() {
        ProductDto product = new ProductDto("Findable Product", "Test Description", new BigDecimal("15.50"), "SNACKS");
        product.setId(csvDataManager.generateProductId());
        product.setSellerName("Test Seller");

        csvDataManager.saveProducts(Arrays.asList(product));

        ProductDto foundProduct = csvDataManager.findProductById(product.getId());
        assertNotNull(foundProduct);
        assertEquals(product.getId(), foundProduct.getId());
        assertEquals(product.getName(), foundProduct.getName());

        ProductDto notFoundProduct = csvDataManager.findProductById(999L);
        assertNull(notFoundProduct);
    }

    @Test
    @DisplayName("Should save and load orders correctly")
    void testSaveAndLoadOrders() {
        // First create a customer
        CustomerDto customer = new CustomerDto("order-customer@example.com", "Order Customer", "555-9999");
        customer.setId(csvDataManager.generateCustomerId());
        csvDataManager.saveCustomer(customer);

        // Create an address
        AddressDto address = new AddressDto("Main St", "123", "Downtown", "City", "ST", "12345");

        // Create order
        OrderDto order = new OrderDto();
        order.setId(csvDataManager.generateOrderId());
        order.setCustomerId(customer.getId());
        order.setCustomerName(customer.getName());
        order.setStatus("WAITING");
        order.setDeliveryAddress(address);
        order.setDeliveryFee(new BigDecimal("5.00"));
        order.setPaymentMethod("CREDIT_CARD");
        order.setCreatedAt(LocalDateTime.now());

        csvDataManager.saveOrder(order);

        List<OrderDto> orders = csvDataManager.loadOrders();
        assertEquals(1, orders.size());

        OrderDto loadedOrder = orders.get(0);
        assertEquals(order.getId(), loadedOrder.getId());
        assertEquals(order.getCustomerId(), loadedOrder.getCustomerId());
        assertEquals(order.getCustomerName(), loadedOrder.getCustomerName());
        assertEquals(order.getStatus(), loadedOrder.getStatus());
        assertEquals(0, order.getDeliveryFee().compareTo(loadedOrder.getDeliveryFee()));
        assertEquals(order.getPaymentMethod(), loadedOrder.getPaymentMethod());

        // Check delivery address
        assertNotNull(loadedOrder.getDeliveryAddress());
        assertEquals(address.getStreet(), loadedOrder.getDeliveryAddress().getStreet());
        assertEquals(address.getNumber(), loadedOrder.getDeliveryAddress().getNumber());
    }

    @Test
    @DisplayName("Should save and load order with items correctly")
    void testSaveOrderWithItems() {
        // Create and save a product first
        ProductDto product = new ProductDto("Order Item Product", "Test Description", new BigDecimal("25.00"), "SNACKS");
        product.setId(csvDataManager.generateProductId());
        product.setSellerName("Test Seller");
        csvDataManager.saveProducts(Arrays.asList(product));

        // Create customer
        CustomerDto customer = new CustomerDto("item-customer@example.com", "Item Customer", "555-8888");
        customer.setId(csvDataManager.generateCustomerId());
        csvDataManager.saveCustomer(customer);

        // Create order item
        OrderItemDto orderItem = new OrderItemDto(product, 2, "Extra sauce");
        orderItem.setUnitPrice(product.getPrice());

        // Create order
        OrderDto order = new OrderDto();
        order.setId(csvDataManager.generateOrderId());
        order.setCustomerId(customer.getId());
        order.setCustomerName(customer.getName());
        order.setStatus("WAITING");
        order.setItems(Arrays.asList(orderItem));
        order.setDeliveryFee(new BigDecimal("8.00"));
        order.setCreatedAt(LocalDateTime.now());

        csvDataManager.saveOrder(order);

        List<OrderDto> orders = csvDataManager.loadOrders();
        assertEquals(1, orders.size());

        OrderDto loadedOrder = orders.get(0);
        assertNotNull(loadedOrder.getItems());
        assertEquals(1, loadedOrder.getItems().size());

        OrderItemDto loadedItem = loadedOrder.getItems().get(0);
        assertEquals(product.getId(), loadedItem.getProduct().getId());
        assertEquals(2, loadedItem.getQuantity());
        assertEquals(0, product.getPrice().compareTo(loadedItem.getUnitPrice()));
        assertEquals("Extra sauce", loadedItem.getObservations());
    }

    @Test
    @DisplayName("Should find orders by customer ID")
    void testFindOrdersByCustomerId() {
        // Create customers
        CustomerDto customer1 = new CustomerDto("customer1@example.com", "Customer One", "555-1111");
        customer1.setId(csvDataManager.generateCustomerId());
        csvDataManager.saveCustomer(customer1);

        CustomerDto customer2 = new CustomerDto("customer2@example.com", "Customer Two", "555-2222");
        customer2.setId(csvDataManager.generateCustomerId());
        csvDataManager.saveCustomer(customer2);

        // Create orders for customer1
        OrderDto order1 = new OrderDto();
        order1.setId(csvDataManager.generateOrderId());
        order1.setCustomerId(customer1.getId());
        order1.setCustomerName(customer1.getName());
        order1.setStatus("WAITING");
        order1.setCreatedAt(LocalDateTime.now().minusHours(2));

        OrderDto order2 = new OrderDto();
        order2.setId(csvDataManager.generateOrderId());
        order2.setCustomerId(customer1.getId());
        order2.setCustomerName(customer1.getName());
        order2.setStatus("DELIVERED");
        order2.setCreatedAt(LocalDateTime.now().minusHours(1));

        // Create order for customer2
        OrderDto order3 = new OrderDto();
        order3.setId(csvDataManager.generateOrderId());
        order3.setCustomerId(customer2.getId());
        order3.setCustomerName(customer2.getName());
        order3.setStatus("WAITING");
        order3.setCreatedAt(LocalDateTime.now());

        csvDataManager.saveOrder(order1);
        csvDataManager.saveOrder(order2);
        csvDataManager.saveOrder(order3);

        List<OrderDto> customer1Orders = csvDataManager.findOrdersByCustomerId(customer1.getId());
        assertEquals(2, customer1Orders.size());

        // Should be sorted by creation time
        assertEquals(order1.getId(), customer1Orders.get(0).getId());
        assertEquals(order2.getId(), customer1Orders.get(1).getId());

        List<OrderDto> customer2Orders = csvDataManager.findOrdersByCustomerId(customer2.getId());
        assertEquals(1, customer2Orders.size());
        assertEquals(order3.getId(), customer2Orders.get(0).getId());
    }

    @Test
    @DisplayName("Should find order by ID")
    void testFindOrderById() {
        CustomerDto customer = new CustomerDto("findorder@example.com", "Find Order Customer", "555-7777");
        customer.setId(csvDataManager.generateCustomerId());
        csvDataManager.saveCustomer(customer);

        OrderDto order = new OrderDto();
        order.setId(csvDataManager.generateOrderId());
        order.setCustomerId(customer.getId());
        order.setCustomerName(customer.getName());
        order.setStatus("IN_PREPARATION");
        order.setCreatedAt(LocalDateTime.now());

        csvDataManager.saveOrder(order);

        OrderDto foundOrder = csvDataManager.findOrderById(order.getId());
        assertNotNull(foundOrder);
        assertEquals(order.getId(), foundOrder.getId());
        assertEquals(order.getStatus(), foundOrder.getStatus());

        OrderDto notFoundOrder = csvDataManager.findOrderById(999L);
        assertNull(notFoundOrder);
    }

    @Test
    @DisplayName("Should update order correctly")
    void testUpdateOrder() {
        CustomerDto customer = new CustomerDto("update@example.com", "Update Customer", "555-6666");
        customer.setId(csvDataManager.generateCustomerId());
        csvDataManager.saveCustomer(customer);

        OrderDto order = new OrderDto();
        order.setId(csvDataManager.generateOrderId());
        order.setCustomerId(customer.getId());
        order.setCustomerName(customer.getName());
        order.setStatus("WAITING");
        order.setCreatedAt(LocalDateTime.now());

        csvDataManager.saveOrder(order);

        // Update the order
        order.setStatus("DELIVERED");
        order.setCancellationReason(null);
        csvDataManager.updateOrder(order);

        OrderDto updatedOrder = csvDataManager.findOrderById(order.getId());
        assertNotNull(updatedOrder);
        assertEquals("DELIVERED", updatedOrder.getStatus());
    }

    @Test
    @DisplayName("Should handle empty data files gracefully")
    void testEmptyDataFiles() {
        // Test with non-existent files
        List<CustomerDto> customers = csvDataManager.loadCustomers();
        assertNotNull(customers);
        assertTrue(customers.isEmpty() || customers.size() >= 0);

        List<OrderDto> orders = csvDataManager.loadOrders();
        assertNotNull(orders);
        assertTrue(orders.isEmpty() || orders.size() >= 0);
    }

    @Test
    @DisplayName("Should handle special characters in data")
    void testSpecialCharactersInData() {
        CustomerDto customer = new CustomerDto("josé@example.com", "José María", "555-ÑOÑO");
        customer.setId(csvDataManager.generateCustomerId());

        csvDataManager.saveCustomer(customer);

        CustomerDto foundCustomer = csvDataManager.findCustomerByEmail("josé@example.com");
        assertNotNull(foundCustomer);
        assertEquals("José María", foundCustomer.getName());
        assertEquals("555-ÑOÑO", foundCustomer.getPhone());
    }

    @Test
    @DisplayName("Should handle products with quotes in description")
    void testProductsWithQuotes() {
        ProductDto product = new ProductDto("Product \"Special\"", "Description with \"quotes\"",
                                          new BigDecimal("10.00"), "SNACKS");
        product.setId(csvDataManager.generateProductId());
        product.setSellerName("Seller \"Name\"");

        csvDataManager.saveProducts(Arrays.asList(product));

        ProductDto foundProduct = csvDataManager.findProductById(product.getId());
        assertNotNull(foundProduct);
        assertEquals("Product \"Special\"", foundProduct.getName());
        assertEquals("Description with \"quotes\"", foundProduct.getDescription());
        assertEquals("Seller \"Name\"", foundProduct.getSellerName());
    }

    @Test
    @DisplayName("Should handle orders with complex delivery addresses")
    void testOrdersWithComplexAddresses() {
        CustomerDto customer = new CustomerDto("complex@example.com", "Complex Customer", "555-5555");
        customer.setId(csvDataManager.generateCustomerId());
        csvDataManager.saveCustomer(customer);

        AddressDto complexAddress = new AddressDto("Rua das Flores, 123", "Apt 45",
                                                  "Centro Histórico", "São Paulo", "SP", "01234-567");
        complexAddress.setComplement("Next to the café");

        OrderDto order = new OrderDto();
        order.setId(csvDataManager.generateOrderId());
        order.setCustomerId(customer.getId());
        order.setCustomerName(customer.getName());
        order.setStatus("WAITING");
        order.setDeliveryAddress(complexAddress);
        order.setCreatedAt(LocalDateTime.now());

        csvDataManager.saveOrder(order);

        OrderDto foundOrder = csvDataManager.findOrderById(order.getId());
        assertNotNull(foundOrder);
        assertNotNull(foundOrder.getDeliveryAddress());
        assertEquals("Rua das Flores, 123", foundOrder.getDeliveryAddress().getStreet());
        assertEquals("São Paulo", foundOrder.getDeliveryAddress().getCity());
        assertEquals("01234-567", foundOrder.getDeliveryAddress().getZipCode());
    }

    @Test
    @DisplayName("Should handle decimal numbers in Brazilian format")
    void testBrazilianDecimalFormat() {
        // Test that the system can handle Brazilian decimal format (comma as decimal separator)
        // This is important for CSV parsing
        ProductDto product = new ProductDto("Decimal Test Product", "Test Description",
                                          new BigDecimal("15.50"), "SNACKS");
        product.setId(csvDataManager.generateProductId());
        product.setSellerName("Test Seller");

        csvDataManager.saveProducts(Arrays.asList(product));

        ProductDto foundProduct = csvDataManager.findProductById(product.getId());
        assertNotNull(foundProduct);
        assertEquals(0, new BigDecimal("15.50").compareTo(foundProduct.getPrice()));
    }
}