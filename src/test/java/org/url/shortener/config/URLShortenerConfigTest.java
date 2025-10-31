package org.url.shortener.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class URLShortenerConfigTest {

    @Test
    void testCreateDefaultConfig() {
        // When
        URLShortenerConfig config = new URLShortenerConfig();

        // Then
        assertEquals(6, config.getShortUrlLength());
        assertEquals(5, config.getMaxCollisionRetryAttempts());
        assertEquals(3600, config.getDefaultUrlExpirySeconds());
        assertEquals(60, config.getCleanupFrequencySeconds());
        assertEquals(2, config.getSchedulerThreadPoolSize());
        assertTrue(config.isUrlValidationEnabled());
        assertTrue(config.isDuplicateLongUrlsPrevented());
    }

    @Test
    void testCreateCustomConfig() {
        // Given
        int shortUrlLength = 8;
        int maxRetryAttempts = 10;
        long defaultExpiry = 7200L;
        int cleanupFrequency = 120;
        int threadPoolSize = 4;
        boolean enableValidation = false;
        boolean preventDuplicates = false;

        // When
        URLShortenerConfig config = new URLShortenerConfig(
            shortUrlLength, maxRetryAttempts, defaultExpiry, 
            cleanupFrequency, threadPoolSize, enableValidation, preventDuplicates
        );

        // Then
        assertEquals(shortUrlLength, config.getShortUrlLength());
        assertEquals(maxRetryAttempts, config.getMaxCollisionRetryAttempts());
        assertEquals(defaultExpiry, config.getDefaultUrlExpirySeconds());
        assertEquals(cleanupFrequency, config.getCleanupFrequencySeconds());
        assertEquals(threadPoolSize, config.getSchedulerThreadPoolSize());
        assertFalse(config.isUrlValidationEnabled());
        assertFalse(config.isDuplicateLongUrlsPrevented());
    }

    @Test
    void testBuilder() {
        // When
        URLShortenerConfig config = URLShortenerConfig.builder()
            .withShortUrlLength(8)
            .withMaxRetryAttempts(10)
            .withDefaultExpiry(7200L)
            .build();

        // Then
        assertEquals(8, config.getShortUrlLength());
        assertEquals(10, config.getMaxCollisionRetryAttempts());
        assertEquals(7200L, config.getDefaultUrlExpirySeconds());
    }

    @Test
    void testBuilder_WithDisabledValidation() {
        // When
        URLShortenerConfig config = URLShortenerConfig.builder()
            .disableValidation()
            .build();

        // Then
        assertFalse(config.isUrlValidationEnabled());
    }
}

