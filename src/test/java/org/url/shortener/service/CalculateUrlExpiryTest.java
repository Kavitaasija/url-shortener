package org.url.shortener.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CalculateUrlExpiryTest {

    @Test
    void testGetDefaultExpiry() {
        // Given
        long expirySeconds = 3600L;
        CalculateUrlExpiry calculator = new CalculateUrlExpiry(expirySeconds);
        long currentTime = Instant.now().getEpochSecond();

        // When
        Long expiry = calculator.getDefaultExpiry();

        // Then
        assertNotNull(expiry);
        assertTrue(expiry > currentTime);
        assertTrue(expiry <= currentTime + expirySeconds + 1); // Allow 1 second tolerance
    }

    @Test
    void testConstructor_WithInvalidExpirySeconds_ThrowsException() {
        // Given
        long invalidExpiry = -100L;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CalculateUrlExpiry(invalidExpiry)
        );
        assertEquals("Expiry seconds must be positive", exception.getMessage());
    }

    @Test
    void testGetCustomExpiry() {
        // Given
        CalculateUrlExpiry calculator = new CalculateUrlExpiry(3600L);
        long customExpirySeconds = 7200L;
        long currentTime = Instant.now().getEpochSecond();

        // When
        Long expiry = calculator.getCustomExpiry(customExpirySeconds);

        // Then
        assertNotNull(expiry);
        assertTrue(expiry > currentTime);
        assertTrue(expiry <= currentTime + customExpirySeconds + 1);
    }

    @Test
    void testGetCustomExpiry_WithInvalidSeconds_ThrowsException() {
        // Given
        CalculateUrlExpiry calculator = new CalculateUrlExpiry(3600L);
        long invalidCustomExpiry = 0L;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> calculator.getCustomExpiry(invalidCustomExpiry)
        );
        assertEquals("Custom expiry seconds must be positive", exception.getMessage());
    }
}

