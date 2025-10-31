package org.url.shortener.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsListenerTest {

    private AnalyticsListener listener;

    @BeforeEach
    void setUp() {
        listener = new AnalyticsListener();
    }

    @Test
    void testOnUrlCreated() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        long expiryTime = 3600L;

        // When
        listener.onUrlCreated(shortUrl, longUrl, expiryTime);

        // Then
        assertEquals(0, listener.getAccessCount(shortUrl));
    }

    @Test
    void testOnUrlAccessed_TracksAccessCount() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        listener.onUrlCreated(shortUrl, longUrl, 3600L);

        // When
        listener.onUrlAccessed(shortUrl, longUrl);
        listener.onUrlAccessed(shortUrl, longUrl);

        // Then
        assertEquals(2, listener.getAccessCount(shortUrl));
    }

    @Test
    void testOnUrlDeleted_CleansUpData() {
        // Given
        String shortUrl = "abc123";
        listener.onUrlCreated(shortUrl, "https://www.example.com", 3600L);
        listener.onUrlAccessed(shortUrl, "https://www.example.com");

        // When
        listener.onUrlDeleted(shortUrl);

        // Then
        assertEquals(0, listener.getAccessCount(shortUrl));
    }

    @Test
    void testGetAccessCount_ForNonExistentUrl() {
        // Given
        String nonExistentUrl = "nonexistent";

        // When
        int count = listener.getAccessCount(nonExistentUrl);

        // Then
        assertEquals(0, count);
    }

    @Test
    void testPrintTopUrls() {
        // Given
        listener.onUrlCreated("url1", "https://example1.com", 3600L);
        listener.onUrlCreated("url2", "https://example2.com", 3600L);
        listener.onUrlAccessed("url1", "https://example1.com");
        listener.onUrlAccessed("url1", "https://example1.com");
        listener.onUrlAccessed("url2", "https://example2.com");

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> listener.printTopUrls(2));
    }
}

