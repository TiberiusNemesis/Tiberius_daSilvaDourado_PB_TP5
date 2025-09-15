package com.ordermanagement;

import com.ordermanagement.server.JavalinServer;

/**
 * Class to run only the Javalin server without the CLI
 */
public class ServerMain {
    
    public static void main(String[] args) {
        System.out.println("=== Order Management Server ===");
        System.out.println("Developed by: Tiberius da Silva Dourado");
        System.out.println("Mode: Server only (without CLI)");
        System.out.println();
        
        // Start the Javalin server
        JavalinServer server = new JavalinServer();
        
        // Add shutdown hook to stop the server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Stopping server...");
            server.stop();
        }));
        
        try {
            // Start the server on port 8080
            server.start(8080);
            
            System.out.println("✅ Server started successfully!");
            System.out.println("🔗 Access http://localhost:8080/health to verify");
            System.out.println("⏹️  Press Ctrl+C to stop the server");
            
            // Keep the server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}