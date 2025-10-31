package org.url.shortener.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggingListenerTest {

    private LoggingListener listener;

    @BeforeEach
    void setUp() {
        listener = new LoggingListener();
    }

    @Test
    void testOnUrlCreated() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        long expiryTime = 3600L;

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> listener.onUrlCreated(shortUrl, longUrl, expiryTime));
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
    void testOnUrlExpired() {
        // Given
        String shortUrl = "abc123";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> listener.onUrlExpired(shortUrl));
    }

    @Test
    void testOnCollisionDetected() {
        // Given
        String shortUrl = "abc123";
        int attemptNumber = 2;

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> listener.onCollisionDetected(shortUrl, attemptNumber));
    }
}

