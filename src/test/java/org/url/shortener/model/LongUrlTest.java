package org.url.shortener.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LongUrlTest {

    @Test
    void testCreateLongUrl() {
        // Given
        String url = "https://www.example.com";
        Long expiry = 3600L;

        // When
        LongUrl longUrl = new LongUrl(url, expiry);

        // Then
        assertEquals(url, longUrl.getUrl());
        assertEquals(expiry, longUrl.getExpiry());
    }

    @Test
    void testCreateLongUrl_WithInvalidExpiry_ThrowsException() {
        // Given
        String url = "https://www.example.com";
        Long invalidExpiry = -100L;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new LongUrl(url, invalidExpiry)
        );
        assertEquals("Expiry must be a positive timestamp", exception.getMessage());
    }

}

