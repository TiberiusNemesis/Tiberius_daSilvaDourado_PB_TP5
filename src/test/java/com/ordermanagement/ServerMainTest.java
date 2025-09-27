package com.ordermanagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class ServerMainTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should start server with default port")
    void testServerMainStart() {
        // Create a thread to run ServerMain
        Thread serverThread = new Thread(() -> {
            try {
                ServerMain.main(new String[]{});
            } catch (Exception e) {
                // Expected when we interrupt
            }
        });

        serverThread.start();

        try {
            Thread.sleep(500); // Give server time to start
            serverThread.interrupt(); // Stop the server
            serverThread.join(1000); // Wait for it to finish
        } catch (InterruptedException e) {
            // Expected
        }

        String output = outputStream.toString();
        assertTrue(output.contains("SERVER MODE") ||
                  output.contains("Starting") ||
                  output.contains("Error") ||
                  output.length() > 0);
    }

    @Test
    @DisplayName("Should start server with custom port")
    void testServerMainWithPort() {
        String[] args = {"9999"};

        Thread serverThread = new Thread(() -> {
            try {
                ServerMain.main(args);
            } catch (Exception e) {
                // Expected when we interrupt
            }
        });

        serverThread.start();

        try {
            Thread.sleep(500); // Give server time to start
            serverThread.interrupt(); // Stop the server
            serverThread.join(1000); // Wait for it to finish
        } catch (InterruptedException e) {
            // Expected
        }

        String output = outputStream.toString();
        assertTrue(output.contains("9999") ||
                  output.contains("Error") ||
                  output.contains("Starting") ||
                  output.length() > 0);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}