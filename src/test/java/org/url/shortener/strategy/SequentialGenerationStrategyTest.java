package org.url.shortener.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequentialGenerationStrategyTest {

    private SequentialGenerationStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SequentialGenerationStrategy();
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
        assertTrue(key.matches("\\d+"));
    }

    @Test
    void testGenerateUniqueKey_GeneratesSequentialKeys() {
        // Given
        String longUrl = "https://www.example.com";
        int length = 6;

        // When
        String key1 = strategy.generateUniqueKey(longUrl, length);
        String key2 = strategy.generateUniqueKey(longUrl, length);

        // Then - Sequential strategy should generate incrementing keys
        long id1 = Long.parseLong(key1);
        long id2 = Long.parseLong(key2);
        assertEquals(id1 + 1, id2);
    }
}

