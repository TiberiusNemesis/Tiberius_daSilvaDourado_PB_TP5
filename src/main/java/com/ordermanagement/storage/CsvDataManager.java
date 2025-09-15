package com.ordermanagement.storage;

import com.ordermanagement.dto.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Locale;

public class CsvDataManager {
    
    private static final String DATA_DIR = "data";
    private static final String CUSTOMERS_FILE = DATA_DIR + "/customers.csv";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.csv";
    private static final String ORDERS_FILE = DATA_DIR + "/orders.csv";
    private static final String ORDER_ITEMS_FILE = DATA_DIR + "/order_items.csv";
    private static final String CUSTOMER_IDS_FILE = DATA_DIR + "/customer_ids.csv";
    private static final String PRODUCT_IDS_FILE = DATA_DIR + "/product_ids.csv";
    private static final String ORDER_IDS_FILE = DATA_DIR + "/order_ids.csv";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AtomicLong customerIdGenerator;
    private final AtomicLong productIdGenerator;
    private final AtomicLong orderIdGenerator;
    
    public CsvDataManager() {
        createDataDirectory();
        this.customerIdGenerator = new AtomicLong(loadLastId(CUSTOMER_IDS_FILE));
        this.productIdGenerator = new AtomicLong(loadLastId(PRODUCT_IDS_FILE));
        this.orderIdGenerator = new AtomicLong(loadLastId(ORDER_IDS_FILE));
        initializeDefaultData();
    }
    
    private void createDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("📁 Data directory created: " + DATA_DIR);
            }
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }
    
    private long loadLastId(String idsFile) {
        try {
            Path idsPath = Paths.get(idsFile);
            if (Files.exists(idsPath)) {
                List<String> lines = Files.readAllLines(idsPath);
                if (!lines.isEmpty()) {
                    return Long.parseLong(lines.get(0));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("⚠️ Creating initial IDs file: " + idsFile);
        }
        return 0L;
    }
    
    private void saveLastId(String idsFile, AtomicLong generator) {
        try {
            Files.write(Paths.get(idsFile),
                       Collections.singletonList(String.valueOf(generator.get())));
        } catch (IOException e) {
            System.err.println("Error saving last ID: " + e.getMessage());
        }
    }
    
    public long generateCustomerId() {
        long id = customerIdGenerator.incrementAndGet();
        saveLastId(CUSTOMER_IDS_FILE, customerIdGenerator);
        return id;
    }

    public long generateProductId() {
        long id = productIdGenerator.incrementAndGet();
        saveLastId(PRODUCT_IDS_FILE, productIdGenerator);
        return id;
    }

    public long generateOrderId() {
        long id = orderIdGenerator.incrementAndGet();
        saveLastId(ORDER_IDS_FILE, orderIdGenerator);
        return id;
    }
    
    private void initializeDefaultData() {
        if (!Files.exists(Paths.get(PRODUCTS_FILE))) {
            System.out.println("🌱 Initializing default data...");
            
            // Initial products
            List<ProductDto> defaultProducts = Arrays.asList(
                createProduct("X-Burger Artesanal", "Artisanal burger with cheddar cheese, lettuce and tomato", 
                             new BigDecimal("25.90"), "SNACKS", "Maria's Snack Bar"),
                createProduct("Pizza Margherita", "Traditional pizza with tomato sauce, mozzarella and basil", 
                             new BigDecimal("35.00"), "SNACKS", "João's Pizza Shop"),
                createProduct("Cola Soda", "Cola flavored soda 350ml", 
                             new BigDecimal("5.50"), "BEVERAGES", "Maria's Snack Bar"),
                createProduct("Milk Pudding", "Homemade condensed milk pudding", 
                             new BigDecimal("8.00"), "DESSERTS", "Ana's Bakery")
            );
            
            saveProducts(defaultProducts);
        }
    }
    
    private ProductDto createProduct(String name, String description, BigDecimal price, 
                                   String category, String sellerName) {
        ProductDto product = new ProductDto(name, description, price, category);
        product.setId(generateProductId());
        product.setSellerName(sellerName);
        return product;
    }
    
    // Customer operations
    public void saveCustomer(CustomerDto customer) {
        try {
            boolean fileExists = Files.exists(Paths.get(CUSTOMERS_FILE));
            try (FileWriter writer = new FileWriter(CUSTOMERS_FILE, true)) {
                if (!fileExists) {
                    writer.write("id,email,name,phone,created_at\n");
                }
                writer.write(String.format("%d,%s,%s,%s,%s\n",
                    customer.getId(), customer.getEmail(), customer.getName(),
                    customer.getPhone(), customer.getCreatedAt().format(DATE_FORMATTER)));
            }
            System.out.println("💾 Customer saved: " + customer.getName());
        } catch (IOException e) {
            System.err.println("Error saving customer: " + e.getMessage());
        }
    }
    
    public List<CustomerDto> loadCustomers() {
        List<CustomerDto> customers = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(CUSTOMERS_FILE))) {
                return customers;
            }
            
            List<String> lines = Files.readAllLines(Paths.get(CUSTOMERS_FILE));
            for (int i = 1; i < lines.size(); i++) { // Skip header
                String line = lines.get(i);
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    CustomerDto customer = new CustomerDto();
                    if (parts[0].trim().isEmpty()) {
                        continue; // Skip line with empty ID
                    }
                    customer.setId(Long.parseLong(parts[0].trim()));
                    customer.setEmail(parts[1]);
                    customer.setName(parts[2]);
                    customer.setPhone(parts[3]);
                    
                    // Validation before date parsing
                    if (parts[4].trim().isEmpty()) {
                        customer.setCreatedAt(LocalDateTime.now()); // Use current date as fallback
                    } else {
                        customer.setCreatedAt(LocalDateTime.parse(parts[4], DATE_FORMATTER));
                    }
                    customers.add(customer);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return customers;
    }
    
    public CustomerDto findCustomerByEmail(String email) {
        return loadCustomers().stream()
            .filter(customer -> customer.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }
    
    // Product operations
    public void saveProducts(List<ProductDto> products) {
        try {
            try (FileWriter writer = new FileWriter(PRODUCTS_FILE, false)) {
                writer.write("id,name,description,price,category,seller_name\n");
                for (ProductDto product : products) {
                    writer.write(String.format(Locale.US, "%d,\"%s\",\"%s\",%.2f,%s,\"%s\"\n",
                        product.getId(), 
                        product.getName().replace("\"", "\"\""), 
                        product.getDescription().replace("\"", "\"\""),
                        product.getPrice(), 
                        product.getCategory(), 
                        product.getSellerName().replace("\"", "\"\"")));
                }
            }
            System.out.println("💾 " + products.size() + " products saved to CSV");
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }
    
    public List<ProductDto> loadProducts() {
        List<ProductDto> products = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(PRODUCTS_FILE))) {
                return products;
            }
            
            List<String> lines = Files.readAllLines(Paths.get(PRODUCTS_FILE));
            for (int i = 1; i < lines.size(); i++) { // Skip header
                String line = lines.get(i);
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = parseCSVLine(line);
                if (parts.length >= 6) {
                    ProductDto product = new ProductDto(
                        parts[1].replace("\"\"", "\""), // Remove quotes
                        parts[2].replace("\"\"", "\""), 
                        new BigDecimal(parts[3]), 
                        parts[4]);
                    if (parts[0].trim().isEmpty()) {
                        continue; // Skip line with empty ID
                    }
                    product.setId(Long.parseLong(parts[0].trim()));
                    product.setSellerName(parts[5].replace("\"\"", "\""));
                    products.add(product);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
        return products;
    }
    
    public ProductDto findProductById(Long id) {
        return loadProducts().stream()
            .filter(product -> product.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    // Order operations
    public void saveOrder(OrderDto order) {
        try {
            boolean fileExists = Files.exists(Paths.get(ORDERS_FILE));
            try (FileWriter writer = new FileWriter(ORDERS_FILE, true)) {
                if (!fileExists) {
                    writer.write("id,customer_id,customer_name,status,delivery_fee,payment_method,created_at,cancellation_reason,delivery_address\n");
                }
                
                String deliveryAddress = "";
                if (order.getDeliveryAddress() != null) {
                    AddressDto addr = order.getDeliveryAddress();
                    deliveryAddress = String.format("%s|%s|%s|%s|%s|%s", 
                        addr.getStreet(), addr.getNumber(), addr.getNeighborhood(),
                        addr.getCity(), addr.getState(), addr.getZipCode());
                }
                
                writer.write(String.format(Locale.US, "%d,%d,%s,%s,%.2f,%s,%s,%s,\"%s\"\n",
                    order.getId(), order.getCustomerId(), order.getCustomerName(),
                    order.getStatus(),
                    order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO,
                    order.getPaymentMethod() != null ? order.getPaymentMethod() : "",
                    order.getCreatedAt().format(DATE_FORMATTER),
                    order.getCancellationReason() != null ? order.getCancellationReason() : "",
                    deliveryAddress));
            }
            
            // Save order items
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                saveOrderItems(order.getId(), order.getItems());
            }
            
            System.out.println("💾 Order saved: #" + order.getId());
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }
    
    private void saveOrderItems(Long orderId, List<OrderItemDto> items) {
        try {
            boolean fileExists = Files.exists(Paths.get(ORDER_ITEMS_FILE));
            try (FileWriter writer = new FileWriter(ORDER_ITEMS_FILE, true)) {
                if (!fileExists) {
                    writer.write("order_id,product_id,product_name,quantity,unit_price,observations\n");
                }
                
                for (OrderItemDto item : items) {
                    writer.write(String.format(Locale.US, "%d,%d,%s,%d,%.2f,%s\n",
                        orderId, item.getProduct().getId(), item.getProduct().getName(),
                        item.getQuantity(), item.getUnitPrice(),
                        item.getObservations() != null ? item.getObservations() : ""));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving order items: " + e.getMessage());
        }
    }
    
    public List<OrderDto> loadOrders() {
        List<OrderDto> orders = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(ORDERS_FILE))) {
                return orders;
            }
            
            List<String> lines = Files.readAllLines(Paths.get(ORDERS_FILE));
            for (int i = 1; i < lines.size(); i++) { // Skip header
                String line = lines.get(i);
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Handle quoted delivery address
                String[] parts = parseCSVLine(line);
                
                if (parts.length >= 9) {
                    OrderDto order = new OrderDto();
                    // Validation before parsing
                    if (parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        continue; // Skip line with empty required fields
                    }
                    order.setId(Long.parseLong(parts[0].trim()));
                    order.setCustomerId(Long.parseLong(parts[1].trim()));
                    order.setCustomerName(parts[2]);
                    order.setStatus(parts[3]);
                    // Handle Brazilian decimal comma format
                    String deliveryFeeStr = parts[4].replace(",", ".");
                    order.setDeliveryFee(new BigDecimal(deliveryFeeStr));
                    order.setPaymentMethod(parts[5]);
                    
                    // Validation before date parsing
                    if (parts[6].trim().isEmpty() || parts[6].trim().equals("00")) {
                        order.setCreatedAt(LocalDateTime.now()); // Use current date as fallback
                    } else {
                        try {
                            order.setCreatedAt(LocalDateTime.parse(parts[6], DATE_FORMATTER));
                        } catch (Exception dateParseException) {
                            System.err.println("Error parsing date '" + parts[6] + "', using current date");
                            order.setCreatedAt(LocalDateTime.now());
                        }
                    }
                    if (!parts[7].isEmpty()) {
                        order.setCancellationReason(parts[7]);
                    }
                    
                    // Parse delivery address
                    if (!parts[8].isEmpty()) {
                        String[] addrParts = parts[8].split("\\|");
                        if (addrParts.length >= 6) {
                            AddressDto address = new AddressDto(
                                addrParts[0], addrParts[1], addrParts[2],
                                addrParts[3], addrParts[4], addrParts[5]);
                            order.setDeliveryAddress(address);
                        }
                    }
                    
                    // Load order items
                    order.setItems(loadOrderItems(order.getId()));
                    orders.add(order);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }
        return orders;
    }
    
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Handle escaped quote ("")
                    current.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }
    
    private List<OrderItemDto> loadOrderItems(Long orderId) {
        List<OrderItemDto> items = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(ORDER_ITEMS_FILE))) {
                return items;
            }
            
            List<String> lines = Files.readAllLines(Paths.get(ORDER_ITEMS_FILE));
            for (int i = 1; i < lines.size(); i++) { // Skip header
                String line = lines.get(i);
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 6 && parts[0].equals(orderId.toString())) {
                    // Validation before parsing
                    if (parts[1].trim().isEmpty() || parts[3].trim().isEmpty()) {
                        continue; // Skip line with empty required fields
                    }
                    ProductDto product = findProductById(Long.parseLong(parts[1].trim()));
                    if (product != null) {
                        OrderItemDto item = new OrderItemDto();
                        item.setProduct(product);
                        item.setQuantity(Integer.parseInt(parts[3].trim()));
                        // Handle Brazilian decimal comma format
                        String unitPriceStr = parts[4].replace(",", ".");
                        item.setUnitPrice(new BigDecimal(unitPriceStr));
                        if (!parts[5].isEmpty() && !parts[5].equals("00")) {
                            item.setObservations(parts[5]);
                        }
                        items.add(item);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading order items: " + e.getMessage());
        }
        return items;
    }
    
    public List<OrderDto> findOrdersByCustomerId(Long customerId) {
        return loadOrders().stream()
            .filter(order -> order.getCustomerId().equals(customerId))
            .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
            .collect(Collectors.toList());
    }
    
    public OrderDto findOrderById(Long orderId) {
        return loadOrders().stream()
            .filter(order -> order.getId().equals(orderId))
            .findFirst()
            .orElse(null);
    }
    
    public void updateOrder(OrderDto order) {
        // To update, we load all orders, replace the specific order and save everything again
        List<OrderDto> orders = loadOrders();
        orders.removeIf(o -> o.getId().equals(order.getId()));
        orders.add(order);
        
        // Rewrite orders file
        try {
            Files.deleteIfExists(Paths.get(ORDERS_FILE));
            Files.deleteIfExists(Paths.get(ORDER_ITEMS_FILE));
        } catch (IOException e) {
            System.err.println("Error deleting old files: " + e.getMessage());
        }
        
        for (OrderDto o : orders) {
            saveOrder(o);
        }
    }
}