package org.url.shortener.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomGenerationStrategyTest {

    private RandomGenerationStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new RandomGenerationStrategy();
    }

    @Test
    void testGenerateUniqueKey() {
        // Given
        String longUrl = "https://www.example.com";
        int length = 6;

        // When
        String key = strategy.generateUniqueKey(longUrl, length);

        // Then
        assertNotNull(key);
        assertEquals(length, key.length());
    }

    @Test
    void testGenerateUniqueKey_GeneratesDifferentKeys() {
        // Given
        String longUrl = "https://www.example.com";
        int length = 6;

        // When
        String key1 = strategy.generateUniqueKey(longUrl, length);
        String key2 = strategy.generateUniqueKey(longUrl, length);

        // Then - Random strategy should generate different keys even for same URL
        assertNotEquals(key1, key2);
    }
}

