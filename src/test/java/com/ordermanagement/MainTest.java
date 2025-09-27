package com.ordermanagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should display system info with --info flag")
    void testSystemInfoFlag() {
        String[] args = {"--info"};

        Main.main(args);

        String output = outputStream.toString();
        assertTrue(output.contains("System Information"));
        assertTrue(output.contains("Functional Requirements"));
        assertTrue(output.contains("RF01"));
        assertTrue(output.contains("Architecture"));
        assertTrue(output.contains("Technologies"));
    }

    @Test
    @DisplayName("Should display system info with -i flag")
    void testSystemInfoShortFlag() {
        String[] args = {"-i"};

        Main.main(args);

        String output = outputStream.toString();
        assertTrue(output.contains("System Information"));
    }

    @Test
    @DisplayName("Should start application without arguments")
    void testMainWithoutArgs() {
        // Provide input to immediately exit the application
        String input = "0\n"; // Exit option
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Create a thread to run main and interrupt it after a short time
        Thread mainThread = new Thread(() -> {
            try {
                Main.main(new String[]{});
            } catch (Exception e) {
                // Expected when we interrupt
            }
        });

        mainThread.start();

        try {
            Thread.sleep(500); // Give it time to start
            mainThread.interrupt(); // Stop the server
            mainThread.join(1000); // Wait for it to finish
        } catch (InterruptedException e) {
            // Expected
        }

        String output = outputStream.toString();
        assertTrue(output.contains("Order Management System"));
        assertTrue(output.contains("Tiberius da Silva Dourado"));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }
}