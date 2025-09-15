package com.ordermanagement.cli;

import com.ordermanagement.api.HttpApiClient;
import com.ordermanagement.dto.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Simplified command line interface
 * that interacts with HttpApiClient to demonstrate the system with Javalin
 */
public class SimpleCLI {
    
    private HttpApiClient apiClient;
    private Scanner scanner;
    private CustomerDto currentCustomer;
    
    public void run() {
        apiClient = new HttpApiClient();
        scanner = new Scanner(System.in);
        
        try {
            showWelcome();
            mainMenu();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                apiClient.close();
                scanner.close();
            } catch (IOException e) {
                System.err.println("Error closing connections: " + e.getMessage());
            }
        }
    }
    
    private void showWelcome() {
        System.out.println("=== Order Management System ===");
        System.out.println("Developed by: Tiberius da Silva Dourado");
        System.out.println("Architecture: CLI + REST API (Mock)");
        System.out.println();
    }
    
    private void mainMenu() throws IOException {
        while (true) {
            System.out.println("=== MAIN MENU ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. View Menu");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    login();
                    pauseBeforeMenu();
                    break;
                case 2:
                    register();
                    pauseBeforeMenu();
                    break;
                case 3:
                    viewMenu();
                    pauseBeforeMenu();
                    break;
                case 0:
                    System.out.println("Thank you for using the system!");
                    return;
                default:
                    System.out.println("Invalid option!");
                    pauseBeforeMenu();
            }
            System.out.println();
        }
    }
    
    private void login() throws IOException {
        System.out.println("=== LOGIN ===");
        
        String email = readValidEmail();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            currentCustomer = apiClient.loginCustomer(email, password);
            System.out.println("Login successful! Welcome, " + currentCustomer.getName());
            customerMenu();
        } catch (IOException e) {
            System.out.println("Login error: " + e.getMessage());
            System.out.println("Tip: Register first to be able to login!");
        }
    }
    
    private void register() throws IOException {
        System.out.println("=== REGISTRATION ===");

        String name = readValidName();
        String email = readValidEmailForRegistration();
        String phone = readValidPhone();
        String password = readValidPassword();
        
        try {
            currentCustomer = apiClient.registerCustomer(email, password, name, phone);
            System.out.println("Registration successful! Welcome, " + currentCustomer.getName());
            customerMenu();
        } catch (IOException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }
    
    private void viewMenu() throws IOException {
        System.out.println("=== MENU ===");
        try {
            List<ProductDto> products = apiClient.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("No products available at the moment.");
                return;
            }
            
            System.out.println("Available products:");
            for (ProductDto product : products) {
                System.out.printf("%d. %s - R$ %.2f%n", 
                                product.getId(), product.getName(), product.getPrice());
                System.out.printf("   %s%n", product.getDescription());
                System.out.printf("   Category: %s | Seller: %s%n",
                                product.getCategory(), product.getSellerName());
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error loading menu: " + e.getMessage());
        }
    }
    
    private void customerMenu() throws IOException {
        while (true) {
            System.out.println("=== CUSTOMER MENU ===");
            System.out.println("Hello, " + currentCustomer.getName() + "!");
            System.out.println("1. View Menu");
            System.out.println("2. Make Order");
            System.out.println("3. My Orders");
            System.out.println("4. Cancel Order");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    viewMenu();
                    pauseBeforeMenu();
                    break;
                case 2:
                    makeOrder();
                    pauseBeforeMenu();
                    break;
                case 3:
                    viewMyOrders();
                    pauseBeforeMenu();
                    break;
                case 4:
                    cancelOrder();
                    pauseBeforeMenu();
                    break;
                case 0:
                    currentCustomer = null;
                    System.out.println("Logout successful!");
                    return;
                default:
                    System.out.println("Invalid option!");
                    pauseBeforeMenu();
            }
            System.out.println();
        }
    }
    
    private void makeOrder() throws IOException {
        System.out.println("=== MAKE ORDER ===");
        
        // Create delivery address (simplified)
        System.out.println("Delivery address:");
        
        String street = readValidStreet();
        String number = readValidNumber();
        String neighborhood = readValidNeighborhood();
        String city = readValidCity();
        String state = readValidState();
        String zipCode = readValidZipCode();
        
        AddressDto address = new AddressDto(street, number, neighborhood, city, state, zipCode);
        
        try {
            // Create order
            OrderDto order = apiClient.createOrder(currentCustomer.getId(), address);

            // Calculate sequential order number for the user
            List<OrderDto> userOrders = apiClient.getOrdersByCustomer(currentCustomer.getId());
            int userOrderNumber = OrderDto.getUserOrderNumber(userOrders, order.getId());

            System.out.println("Order created - Number: #" + userOrderNumber);
            
            // Show menu
            viewMenu();

            // Add items
            while (true) {
                System.out.print("Enter product ID (0 to finish): ");
                Long productId = (long) readInt();
                
                if (productId == 0) break;
                
                int quantity = readValidQuantity();
                System.out.print("Observations (optional): ");
                String observations = scanner.nextLine().trim();
                if (observations.isEmpty()) {
                    observations = null;
                }
                
                try {
                    order = apiClient.addItemToOrder(order.getId(), productId, quantity, observations);
                    System.out.println("Item added to order!");
                    System.out.printf("Current subtotal: R$ %.2f%n", order.getSubtotal());
                } catch (IOException e) {
                    System.out.println("Error adding item: " + e.getMessage());
                }
            }
            
            if (order.getItems() == null || order.getItems().isEmpty()) {
                System.out.println("Order cancelled - no items added.");
                return;
            }
            
            // Finalize order
            System.out.println("\n=== FINALIZE ORDER ===");
            System.out.println("Available payment methods:");
            System.out.println("1. Credit Card");
            System.out.println("2. Debit Card");
            System.out.println("3. PIX");
            System.out.print("Choose payment method (1-3): ");
            int paymentChoice = readInt();
            
            String paymentMethod;
            switch (paymentChoice) {
                case 1:
                    paymentMethod = "CREDIT_CARD";
                    break;
                case 2:
                    paymentMethod = "DEBIT_CARD";
                    break;
                case 3:
                    paymentMethod = "PIX";
                    break;
                default:
                    paymentMethod = "PIX";
            }
            
            // Generate random delivery fee between R$ 1.00 and R$ 10.00
            BigDecimal deliveryFee = BigDecimal.valueOf(1 + Math.random() * 9).setScale(2, BigDecimal.ROUND_HALF_UP);
            System.out.printf("Automatically generated delivery fee: R$ %.2f%n", deliveryFee);

            order = apiClient.finalizeOrder(order.getId(), paymentMethod, deliveryFee);
            
            System.out.println("\n🎉 Order finalized successfully!");
            System.out.printf("Subtotal: R$ %.2f%n", order.getSubtotal());
            System.out.printf("Delivery fee: R$ %.2f%n", order.getDeliveryFee());
            System.out.printf("TOTAL: R$ %.2f%n", order.getTotal());
            System.out.println("Status: " + order.getStatus());
            System.out.println("Payment method: " + order.getPaymentMethod());
            
        } catch (IOException e) {
            System.out.println("Error making order: " + e.getMessage());
        }
    }
    
    private void viewMyOrders() throws IOException {
        System.out.println("=== MY ORDERS ===");
        try {
            List<OrderDto> orders = apiClient.getOrdersByCustomer(currentCustomer.getId());
            
            if (orders.isEmpty()) {
                System.out.println("You have no orders.");
                return;
            }
            
            for (OrderDto order : orders) {
                int userOrderNumber = OrderDto.getUserOrderNumber(orders, order.getId());
                System.out.printf("\n📋 Order #%d%n", userOrderNumber);
                System.out.printf("Status: %s%n", order.getStatus());
                System.out.printf("Total: R$ %.2f%n", order.getTotal());
                System.out.printf("Date: %s%n", order.getCreatedAt());
                
                if (order.getCancellationReason() != null) {
                    System.out.printf("Cancellation reason: %s%n", order.getCancellationReason());
                }
                
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    System.out.println("Items:");
                    for (OrderItemDto item : order.getItems()) {
                        System.out.printf("  • %s (x%d) - R$ %.2f%n", 
                                        item.getProduct().getName(), item.getQuantity(), item.getSubtotal());
                        if (item.getObservations() != null && !item.getObservations().isEmpty()) {
                            System.out.printf("    Obs: %s%n", item.getObservations());
                        }
                    }
                }
                
                if (order.getDeliveryAddress() != null) {
                    System.out.printf("Address: %s%n", order.getDeliveryAddress().getFullAddress());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
    }
    
    private void cancelOrder() throws IOException {
        System.out.println("=== CANCEL ORDER ===");
        viewMyOrders();
        
        System.out.print("\nEnter order number to cancel (0 to go back): ");
        int userOrderNumber = readInt();

        if (userOrderNumber == 0) return;

        // Get all user orders to convert sequential number to real ID
        List<OrderDto> orders = apiClient.getOrdersByCustomer(currentCustomer.getId());
        if (userOrderNumber > orders.size()) {
            System.out.println("❌ Invalid order number!");
            return;
        }

        // Find the order with the specified sequential number
        List<OrderDto> sortedOrders = orders.stream()
            .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
            .collect(java.util.stream.Collectors.toList());

        OrderDto targetOrder = sortedOrders.get(userOrderNumber - 1);
        Long realOrderId = targetOrder.getId();

        System.out.print("Cancellation reason: ");
        String reason = scanner.nextLine();

        try {
            OrderDto order = apiClient.cancelOrder(realOrderId, reason);
            System.out.println("✅ Order cancelled successfully!");
            System.out.println("Status: " + order.getStatus());
        } catch (IOException e) {
            System.out.println("❌ Error cancelling order: " + e.getMessage());
        }
    }
    
    private int readInt() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
    
    private BigDecimal readBigDecimal() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid value (e.g.: 5.50): ");
            }
        }
    }

    private void pauseBeforeMenu() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Data input validations
    private String readValidName() {
        while (true) {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty!");
                continue;
            }
            if (name.length() < 2) {
                System.out.println("❌ Name must have at least 2 characters!");
                continue;
            }
            if (!name.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                System.out.println("❌ Name must contain only letters and spaces!");
                continue;
            }
            return name;
        }
    }
    
    private String readValidEmail() {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
        while (true) {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("❌ Email cannot be empty!");
                continue;
            }
            if (!emailPattern.matcher(email).matches()) {
                System.out.println("❌ Invalid email format! Use the format: example@domain.com");
                continue;
            }
            return email;
        }
    }

    private String readValidEmailForRegistration() {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
        while (true) {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("❌ Email cannot be empty!");
                continue;
            }
            if (!emailPattern.matcher(email).matches()) {
                System.out.println("❌ Invalid email format! Use the format: example@domain.com");
                continue;
            }

            // Check if email already exists in database
            try {
                CustomerDto existingCustomer = apiClient.findCustomerByEmail(email);
                if (existingCustomer != null) {
                    System.out.println("❌ This email is already registered! Use another email or login.");
                    continue;
                }
            } catch (IOException e) {
                // If there's an error in verification, continue with registration
                System.out.println("⚠️ Could not verify duplicate email. Continuing...");
            }

            return email;
        }
    }
    
    private String readValidPhone() {
        while (true) {
            System.out.print("Phone (numbers only): ");
            String phone = scanner.nextLine().trim().replaceAll("[^0-9]", "");
            if (phone.isEmpty()) {
                System.out.println("❌ Phone cannot be empty!");
                continue;
            }
            if (phone.length() < 10 || phone.length() > 11) {
                System.out.println("❌ Phone must have 10 or 11 digits!");
                continue;
            }
            return phone;
        }
    }
    
    private String readValidPassword() {
        while (true) {
            System.out.print("Password (minimum 6 characters): ");
            String password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("❌ Password cannot be empty!");
                continue;
            }
            if (password.length() < 6) {
                System.out.println("❌ Password must have at least 6 characters!");
                continue;
            }
            return password;
        }
    }
    
    private String readValidStreet() {
        while (true) {
            System.out.print("Street: ");
            String street = scanner.nextLine().trim();
            if (street.isEmpty()) {
                System.out.println("❌ Street name cannot be empty!");
                continue;
            }
            if (street.length() < 3) {
                System.out.println("❌ Street name must have at least 3 characters!");
                continue;
            }
            return street;
        }
    }
    
    private String readValidNumber() {
        while (true) {
            System.out.print("Number: ");
            String number = scanner.nextLine().trim();
            if (number.isEmpty()) {
                System.out.println("❌ Number cannot be empty!");
                continue;
            }
            // Validate if it contains at least one digit
            if (!number.matches(".*[0-9].*")) {
                System.out.println("❌ Number must contain at least one digit!");
                continue;
            }
            // Allow numbers with letters (e.g.: 123A, Apt 45)
            if (!number.matches("[0-9A-Za-z\\s/-]+")) {
                System.out.println("❌ Number can contain only letters, numbers, spaces, '/' and '-'!");
                continue;
            }
            return number;
        }
    }
    
    private String readValidNeighborhood() {
        while (true) {
            System.out.print("Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();
            if (neighborhood.isEmpty()) {
                System.out.println("❌ Neighborhood cannot be empty!");
                continue;
            }
            if (neighborhood.length() < 2) {
                System.out.println("❌ Neighborhood must have at least 2 characters!");
                continue;
            }
            return neighborhood;
        }
    }
    
    private String readValidCity() {
        while (true) {
            System.out.print("City: ");
            String city = scanner.nextLine().trim();
            if (city.isEmpty()) {
                System.out.println("❌ City cannot be empty!");
                continue;
            }
            if (city.length() < 2) {
                System.out.println("❌ City must have at least 2 characters!");
                continue;
            }
            if (!city.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                System.out.println("❌ City must contain only letters and spaces!");
                continue;
            }
            return city;
        }
    }
    
    private String readValidState() {
        while (true) {
            System.out.print("State (2 letters, e.g.: SP): ");
            String state = scanner.nextLine().trim().toUpperCase();
            if (state.isEmpty()) {
                System.out.println("❌ State cannot be empty!");
                continue;
            }
            if (state.length() != 2) {
                System.out.println("❌ State must have exactly 2 letters! (e.g.: SP, RJ, MG)");
                continue;
            }
            if (!state.matches("[A-Z]{2}")) {
                System.out.println("❌ State must contain only letters! (e.g.: SP, RJ, MG)");
                continue;
            }
            return state;
        }
    }
    
    private String readValidZipCode() {
        while (true) {
            System.out.print("ZIP Code (numbers only): ");
            String zipCode = scanner.nextLine().trim().replaceAll("[^0-9]", "");
            if (zipCode.isEmpty()) {
                System.out.println("❌ ZIP Code cannot be empty!");
                continue;
            }
            if (zipCode.length() != 8) {
                System.out.println("❌ ZIP Code must have exactly 8 numbers! (e.g.: 01234567)");
                continue;
            }
            // Format ZIP Code in XXXXX-XXX pattern
            return zipCode.substring(0, 5) + "-" + zipCode.substring(5);
        }
    }
    
    private int readValidQuantity() {
        while (true) {
            System.out.print("Quantity: ");
            try {
                String input = scanner.nextLine().trim();
                int quantity = Integer.parseInt(input);
                if (quantity <= 0) {
                    System.out.println("❌ Quantity must be greater than zero!");
                    continue;
                }
                if (quantity > 50) {
                    System.out.println("❌ Quantity must be less than 50!");
                    continue;
                }
                return quantity;
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
            }
        }
    }
}