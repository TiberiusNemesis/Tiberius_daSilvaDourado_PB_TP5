package com.ordermanagement.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ordermanagement.dto.*;
import com.ordermanagement.storage.CsvDataManager;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinJackson;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class JavalinServer {
    
    private final CsvDataManager dataManager;
    
    private Javalin app;
    
    public JavalinServer() {
        this.dataManager = new CsvDataManager();
    }
    
    public void start(int port) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper));
        }).start(port);
        
        setupRoutes();
        
        // Load existing data
        List<ProductDto> products = dataManager.loadProducts();
        System.out.println("📊 " + products.size() + " products loaded from CSV");
        
        System.out.println("🚀 Javalin Server started on port " + port);
        System.out.println("📡 REST API available at http://localhost:" + port);
    }
    
    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
    
    
    private void setupRoutes() {
        // Customer endpoints
        app.post("/api/customers/register", this::registerCustomer);
        app.post("/api/customers/login", this::loginCustomer);
        app.get("/api/customers/email/{email}", this::findCustomerByEmail);
        
        // Product endpoints
        app.get("/api/products", this::getAllProducts);
        app.get("/api/products/{id}", this::getProductById);
        
        // Order endpoints
        app.post("/api/orders", this::createOrder);
        app.post("/api/orders/{id}/items", this::addItemToOrder);
        app.post("/api/orders/{id}/finalize", this::finalizeOrder);
        app.post("/api/orders/{id}/cancel", this::cancelOrder);
        app.get("/api/orders/customer/{customerId}", this::getOrdersByCustomer);
        app.get("/api/orders/{id}", this::getOrderById);
        
        // Health check
        app.get("/health", ctx -> ctx.json(Map.of("status", "OK")));
    }
    
    private void registerCustomer(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            String name = (String) body.get("name");
            String phone = (String) body.get("phone");
            
            if (dataManager.findCustomerByEmail(email) != null) {
                ctx.status(400).json(Map.of("error", "Email already registered"));
                return;
            }
            
            CustomerDto customer = new CustomerDto();
            customer.setId(dataManager.generateCustomerId());
            customer.setEmail(email);
            customer.setName(name);
            customer.setPhone(phone);
            customer.setCreatedAt(LocalDateTime.now());
            
            dataManager.saveCustomer(customer);
            
            ctx.json(customer);
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Invalid data: " + e.getMessage()));
        }
    }
    
    private void loginCustomer(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String email = (String) body.get("email");
            String password = (String) body.get("password");
            
            CustomerDto customer = dataManager.findCustomerByEmail(email);
            if (customer == null) {
                ctx.status(401).json(Map.of("error", "Invalid credentials"));
                return;
            }
            
            ctx.json(customer);
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Invalid data: " + e.getMessage()));
        }
    }
    
    private void getAllProducts(Context ctx) {
        ctx.json(dataManager.loadProducts());
    }
    
    private void getProductById(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            ProductDto product = dataManager.findProductById(id);
            if (product == null) {
                ctx.status(404).json(Map.of("error", "Product not found"));
                return;
            }
            ctx.json(product);
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid ID"));
        }
    }
    
    private void createOrder(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            
            // CustomerId validation
            if (body.get("customerId") == null || body.get("customerId").toString().trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "customerId is required"));
                return;
            }
            
            Long customerId = Long.parseLong(body.get("customerId").toString().trim());
            Map<String, Object> addressMap = (Map<String, Object>) body.get("deliveryAddress");
            
            List<CustomerDto> customers = dataManager.loadCustomers();
            CustomerDto customer = customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst().orElse(null);
            
            if (customer == null) {
                ctx.status(404).json(Map.of("error", "Customer not found"));
                return;
            }
            
            AddressDto address = new AddressDto(
                (String) addressMap.get("street"),
                (String) addressMap.get("number"),
                (String) addressMap.get("neighborhood"),
                (String) addressMap.get("city"),
                (String) addressMap.get("state"),
                (String) addressMap.get("zipCode")
            );
            
            OrderDto order = new OrderDto();
            order.setId(dataManager.generateOrderId());
            order.setCustomerId(customer.getId());
            order.setCustomerName(customer.getName());
            order.setDeliveryAddress(address);
            order.setStatus("PENDING");
            order.setCreatedAt(LocalDateTime.now());
            order.setItems(new ArrayList<>());
            
            dataManager.saveOrder(order);
            
            ctx.json(order);
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Error creating order: " + e.getMessage()));
        }
    }
    
    private void addItemToOrder(Context ctx) {
        try {
            // OrderId path parameter validation
            String orderIdParam = ctx.pathParam("id");
            if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "orderId is required"));
                return;
            }
            
            Long orderId = Long.parseLong(orderIdParam.trim());
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            
            // Required fields validation
            if (body.get("productId") == null || body.get("productId").toString().trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "productId is required"));
                return;
            }
            if (body.get("quantity") == null || body.get("quantity").toString().trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "quantity is required"));
                return;
            }
            
            Long productId = Long.parseLong(body.get("productId").toString().trim());
            int quantity = Integer.parseInt(body.get("quantity").toString().trim());
            
            // Quantity validation
            if (quantity <= 0) {
                ctx.status(400).json(Map.of("error", "Quantity must be greater than zero"));
                return;
            }
            
            String observations = body.get("observations") != null ? body.get("observations").toString() : null;
            
            OrderDto order = dataManager.findOrderById(orderId);
            if (order == null) {
                ctx.status(404).json(Map.of("error", "Order not found"));
                return;
            }
            
            ProductDto product = dataManager.findProductById(productId);
            if (product == null) {
                ctx.status(404).json(Map.of("error", "Product not found"));
                return;
            }
            
            OrderItemDto item = new OrderItemDto();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setObservations(observations);
            item.setUnitPrice(product.getPrice());
            
            order.getItems().add(item);
            dataManager.updateOrder(order);
            
            ctx.json(order);
        } catch (Exception e) {
            System.err.println("Detailed error adding item: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            ctx.status(400).json(Map.of("error", "Error adding item: " + e.getMessage()));
        }
    }
    
    private void finalizeOrder(Context ctx) {
        try {
            Long orderId = Long.parseLong(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            
            // Required fields validation
            if (body.get("paymentMethod") == null || body.get("paymentMethod").toString().trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "paymentMethod is required"));
                return;
            }
            String paymentMethod = body.get("paymentMethod").toString().trim();

            // Generate random delivery fee between R$ 1.00 and R$ 10.00
            BigDecimal deliveryFee = BigDecimal.valueOf(1 + Math.random() * 9).setScale(2, BigDecimal.ROUND_HALF_UP);
            
            OrderDto order = dataManager.findOrderById(orderId);
            if (order == null) {
                ctx.status(404).json(Map.of("error", "Order not found"));
                return;
            }
            
            order.setPaymentMethod(paymentMethod);
            order.setDeliveryFee(deliveryFee);
            order.setStatus("CONFIRMED");
            
            dataManager.updateOrder(order);
            
            ctx.json(order);
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Error finalizing order: " + e.getMessage()));
        }
    }
    
    private void cancelOrder(Context ctx) {
        try {
            Long orderId = Long.parseLong(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            String reason = (String) body.get("reason");
            
            OrderDto order = dataManager.findOrderById(orderId);
            if (order == null) {
                ctx.status(404).json(Map.of("error", "Order not found"));
                return;
            }
            
            if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
                ctx.status(400).json(Map.of("error", "Order cannot be cancelled"));
                return;
            }
            
            order.setStatus("CANCELLED");
            order.setCancellationReason(reason);
            
            dataManager.updateOrder(order);
            
            ctx.json(order);
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", "Error cancelling order: " + e.getMessage()));
        }
    }
    
    private void getOrdersByCustomer(Context ctx) {
        try {
            Long customerId = Long.parseLong(ctx.pathParam("customerId"));
            
            List<OrderDto> customerOrders = dataManager.findOrdersByCustomerId(customerId);
                
            ctx.json(customerOrders);
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid ID"));
        }
    }
    
    private void getOrderById(Context ctx) {
        try {
            Long orderId = Long.parseLong(ctx.pathParam("id"));
            OrderDto order = dataManager.findOrderById(orderId);
            if (order == null) {
                ctx.status(404).json(Map.of("error", "Order not found"));
                return;
            }
            ctx.json(order);
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid ID"));
        }
    }

    private void findCustomerByEmail(Context ctx) {
        try {
            String email = ctx.pathParam("email");
            CustomerDto customer = dataManager.findCustomerByEmail(email);
            if (customer == null) {
                ctx.status(404).json(Map.of("error", "Customer not found"));
                return;
            }
            ctx.json(customer);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}