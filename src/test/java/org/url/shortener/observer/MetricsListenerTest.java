package org.url.shortener.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsListenerTest {

    private MetricsListener listener;

    @BeforeEach
    void setUp() {
        listener = new MetricsListener();
    }

    @Test
    void testOnUrlCreated() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        long expiryTime = 3600L;

        // When
        listener.onUrlCreated(shortUrl, longUrl, expiryTime);

        // Then - should not throw exception
        assertDoesNotThrow(() -> listener.printSummary());
    }

    @Test
    void testOnUrlAccessed() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> listener.onUrlAccessed(shortUrl, longUrl));
    }

    @Test
    void testOnUrlDeleted() {
        // Given
        String shortUrl = "abc123";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> listener.onUrlDeleted(shortUrl));
    }

    @Test
    void testPrintSummary() {
        // Given
        listener.onUrlCreated("url1", "https://example1.com", 3600L);
        listener.onUrlCreated("url2", "https://example2.com", 3600L);
        listener.onUrlAccessed("url1", "https://example1.com");

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> listener.printSummary());
    }
}

