package com.ordermanagement;

import com.ordermanagement.cli.SimpleCLI;
import com.ordermanagement.server.JavalinServer;

/**
 * Main class of the Order Management System
 *
 * System implemented based on functional requirements:
 * RF01 - Customer registration/login
 * RF02 - Menu visualization
 * RF03 - Order registration
 * RF04 - Payment method registration
 * RF05 - Order status changes and visualization
 * RF06 - Order cancellation
 *
 * Architecture: Command line interface that interacts with back-end via REST API with Javalin
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Order Management System ===");
        System.out.println("Developed by: Tiberius da Silva Dourado");
        System.out.println("Architecture: CLI + REST API with Javalin");
        System.out.println();

        // Check if system information should be displayed
        if (args.length > 0 && (args[0].equals("--info") || args[0].equals("-i"))) {
            showSystemInfo();
            return;
        }

        // Start Javalin server in background
        JavalinServer server = new JavalinServer();

        // Add shutdown hook to stop the server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Stopping server...");
            server.stop();
        }));

        try {
            // Start server on port 8080
            server.start(8080);

            // Wait a bit for the server to initialize
            Thread.sleep(1000);

            // Execute CLI that connects to the server
            SimpleCLI cli = new SimpleCLI();
            cli.run();

        } catch (Exception e) {
            System.err.println("Error starting the system: " + e.getMessage());
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }

    private static void showSystemInfo() {
        System.out.println("=== System Information ===");
        System.out.println();

        System.out.println("Implemented Functional Requirements:");
        System.out.println("✓ RF01 - Customer registration/login");
        System.out.println("✓ RF02 - Menu visualization");
        System.out.println("✓ RF03 - Order registration");
        System.out.println("✓ RF04 - Payment method registration");
        System.out.println("✓ RF05 - Order status changes and visualization");
        System.out.println("✓ RF06 - Order cancellation");
        System.out.println();

        System.out.println("Architecture:");
        System.out.println("- CLI Interface (Command Line Interface)");
        System.out.println("- Communication via REST API");
        System.out.println("- HTTP Client for requests");
        System.out.println("- DTOs for data transfer");
        System.out.println("- Clear separation between client and server");
        System.out.println();

        System.out.println("Technologies:");
        System.out.println("- Java 11+");
        System.out.println("- PicoCLI for command line interface");
        System.out.println("- Apache HttpClient for HTTP requests");
        System.out.println("- Jackson for JSON serialization");
        System.out.println("- Maven for dependency management");
        System.out.println();

        System.out.println("How to use:");
        System.out.println("java -jar order-management-system.jar");
        System.out.println("or");
        System.out.println("mvn exec:java");
        System.out.println();

        System.out.println("Command line options:");
        System.out.println("--server, -s <URL>    API server URL (default: http://localhost:8080)");
        System.out.println("--help, -h            Show help");
        System.out.println("--info, -i            Show system information");
        System.out.println();

        System.out.println("Expected API endpoints:");
        System.out.println("POST /api/customers/login");
        System.out.println("POST /api/customers/register");
        System.out.println("GET  /api/products");
        System.out.println("GET  /api/products/category/{category}");
        System.out.println("GET  /api/products/{id}");
        System.out.println("POST /api/orders");
        System.out.println("POST /api/orders/{id}/items");
        System.out.println("POST /api/orders/{id}/finalize");
        System.out.println("POST /api/orders/{id}/cancel");
        System.out.println("GET  /api/orders/customer/{customerId}");
        System.out.println("GET  /api/orders/{id}");
    }
}