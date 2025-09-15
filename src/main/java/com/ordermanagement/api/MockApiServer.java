package com.ordermanagement.api;

import com.ordermanagement.dto.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock API Server to simulate the REST back-end
 * In a real implementation, this would be replaced by a Spring Boot server
 */
public class MockApiServer {
    
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, CustomerDto> customers = new HashMap<>();
    private final Map<Long, ProductDto> products = new HashMap<>();
    private final Map<Long, OrderDto> orders = new HashMap<>();
    
    public MockApiServer() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        // Mock products
        ProductDto burger = new ProductDto("X-Burger Artesanal", 
            "Artisanal burger with cheddar cheese, lettuce and tomato", 
            new BigDecimal("25.90"), "SNACKS");
        burger.setId(idGenerator.getAndIncrement());
        burger.setSellerName("Maria's Snack Bar");
        products.put(burger.getId(), burger);
        
        ProductDto pizza = new ProductDto("Pizza Margherita", 
            "Traditional pizza with tomato sauce, mozzarella and basil", 
            new BigDecimal("35.00"), "SNACKS");
        pizza.setId(idGenerator.getAndIncrement());
        pizza.setSellerName("João's Pizza Shop");
        products.put(pizza.getId(), pizza);
        
        ProductDto soda = new ProductDto("Cola Soda",
            "Cola flavored soda 350ml", 
            new BigDecimal("5.50"), "BEVERAGES");
        soda.setId(idGenerator.getAndIncrement());
        soda.setSellerName("Maria's Snack Bar");
        products.put(soda.getId(), soda);
        
        ProductDto dessert = new ProductDto("Milk Pudding",
            "Homemade condensed milk pudding", 
            new BigDecimal("8.00"), "DESSERTS");
        dessert.setId(idGenerator.getAndIncrement());
        dessert.setSellerName("Ana's Bakery");
        products.put(dessert.getId(), dessert);
        
        System.out.println("Mock API Server initialized with " + products.size() + " products");
    }
    
    // Simulate API endpoints
    
    public CustomerDto loginCustomer(String email, String password) throws ApiException {
        // Search for existing customer
        for (CustomerDto customer : customers.values()) {
            if (customer.getEmail().equals(email)) {
                // In a real implementation, would verify the password
                return customer;
            }
        }
        throw new ApiException("Invalid credentials");
    }
    
    public CustomerDto registerCustomer(String email, String password, String name, String phone) throws ApiException {
        // Check if email already exists
        for (CustomerDto customer : customers.values()) {
            if (customer.getEmail().equals(email)) {
                throw new ApiException("Email already registered");
            }
        }
        
        CustomerDto customer = new CustomerDto(email, name, phone);
        customer.setId(idGenerator.getAndIncrement());
        customers.put(customer.getId(), customer);
        
        return customer;
    }
    
    public List<ProductDto> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public List<ProductDto> getProductsByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }
    
    public ProductDto getProductById(Long id) throws ApiException {
        ProductDto product = products.get(id);
        if (product == null) {
            throw new ApiException("Product not found");
        }
        return product;
    }
    
    public OrderDto createOrder(Long customerId, AddressDto deliveryAddress) throws ApiException {
        CustomerDto customer = customers.get(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        
        OrderDto order = new OrderDto();
        order.setId(idGenerator.getAndIncrement());
        order.setCustomerId(customerId);
        order.setCustomerName(customer.getName());
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus("WAITING");
        order.setItems(new ArrayList<>());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setDeliveryFee(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        
        orders.put(order.getId(), order);
        return order;
    }
    
    public OrderDto addItemToOrder(Long orderId, Long productId, int quantity, String observations) throws ApiException {
        OrderDto order = orders.get(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }
        
        if (!"WAITING".equals(order.getStatus())) {
            throw new ApiException("Cannot modify order that is not waiting");
        }
        
        ProductDto product = products.get(productId);
        if (product == null) {
            throw new ApiException("Product not found");
        }
        
        OrderItemDto item = new OrderItemDto(product, quantity, observations);
        item.setId(idGenerator.getAndIncrement());
        order.getItems().add(item);
        order.setUpdatedAt(LocalDateTime.now());
        
        return order;
    }
    
    public OrderDto finalizeOrder(Long orderId, String paymentMethod, BigDecimal deliveryFee) throws ApiException {
        OrderDto order = orders.get(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }

        order.setPaymentMethod(paymentMethod);
        // Generate random delivery fee between R$ 1.00 and R$ 10.00
        BigDecimal randomDeliveryFee = BigDecimal.valueOf(1 + Math.random() * 9).setScale(2, BigDecimal.ROUND_HALF_UP);
        order.setDeliveryFee(randomDeliveryFee);
        order.setStatus("IN_PREPARATION");
        order.setUpdatedAt(LocalDateTime.now());

        return order;
    }
    
    public OrderDto cancelOrder(Long orderId, String reason) throws ApiException {
        OrderDto order = orders.get(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }
        
        if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new ApiException("Cannot cancel order with status " + order.getStatus());
        }
        
        order.setStatus("CANCELLED");
        order.setCancellationReason(reason);
        order.setUpdatedAt(LocalDateTime.now());
        
        return order;
    }
    
    public List<OrderDto> getOrdersByCustomer(Long customerId) throws ApiException {
        CustomerDto customer = customers.get(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        
        List<OrderDto> customerOrders = new ArrayList<>();
        for (OrderDto order : orders.values()) {
            if (order.getCustomerId().equals(customerId)) {
                customerOrders.add(order);
            }
        }
        
        return customerOrders;
    }
    
    public OrderDto getOrderById(Long orderId) throws ApiException {
        OrderDto order = orders.get(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }
        return order;
    }
    
    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }
    }
}