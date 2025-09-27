package com.ordermanagement.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class JavalinServerTest {

    private JavalinServer server;

    @BeforeEach
    void setUp() {
        server = new JavalinServer();
    }

    @Test
    @DisplayName("Should start and stop server")
    void testStartAndStop() {
        // Start on a random high port to avoid conflicts
        int testPort = 9999 + (int)(Math.random() * 1000);

        assertDoesNotThrow(() -> {
            server.start(testPort);
            Thread.sleep(100); // Give server time to start
        });

        // Server is started, now stop it
        assertDoesNotThrow(() -> server.stop());
    }

    @Test
    @DisplayName("Should handle multiple stop calls")
    void testMultipleStops() {
        assertDoesNotThrow(() -> {
            server.stop();
            server.stop(); // Should not throw
        });
    }

    @Test
    @DisplayName("Should create server instance")
    void testServerCreation() {
        assertNotNull(server);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}