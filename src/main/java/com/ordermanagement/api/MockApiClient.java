package com.ordermanagement.api;

import com.ordermanagement.dto.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Mock API Client that uses MockApiServer instead of HTTP requests
 * For demonstration purposes without needing a real server
 */
public class MockApiClient {
    private final MockApiServer mockServer;
    
    public MockApiClient() {
        this.mockServer = new MockApiServer();
    }
    
    // Customer endpoints
    public CustomerDto loginCustomer(String email, String password) throws IOException {
        try {
            return mockServer.loginCustomer(email, password);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public CustomerDto registerCustomer(String email, String password, String name, String phone) throws IOException {
        try {
            return mockServer.registerCustomer(email, password, name, phone);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    // Product endpoints
    public List<ProductDto> getAllProducts() throws IOException {
        return mockServer.getAllProducts();
    }
    
    public List<ProductDto> getProductsByCategory(String category) throws IOException {
        return mockServer.getProductsByCategory(category);
    }
    
    public ProductDto getProductById(Long id) throws IOException {
        try {
            return mockServer.getProductById(id);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    // Order endpoints
    public OrderDto createOrder(Long customerId, AddressDto deliveryAddress) throws IOException {
        try {
            return mockServer.createOrder(customerId, deliveryAddress);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public OrderDto addItemToOrder(Long orderId, Long productId, int quantity, String observations) throws IOException {
        try {
            return mockServer.addItemToOrder(orderId, productId, quantity, observations);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public OrderDto finalizeOrder(Long orderId, String paymentMethod, BigDecimal deliveryFee) throws IOException {
        try {
            return mockServer.finalizeOrder(orderId, paymentMethod, deliveryFee);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public OrderDto cancelOrder(Long orderId, String reason) throws IOException {
        try {
            return mockServer.cancelOrder(orderId, reason);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public List<OrderDto> getOrdersByCustomer(Long customerId) throws IOException {
        try {
            return mockServer.getOrdersByCustomer(customerId);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public OrderDto getOrderById(Long orderId) throws IOException {
        try {
            return mockServer.getOrderById(orderId);
        } catch (MockApiServer.ApiException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public void close() throws IOException {
        // Nothing to close in mock implementation
    }
}