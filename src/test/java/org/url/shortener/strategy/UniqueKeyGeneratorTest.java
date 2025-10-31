package org.url.shortener.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniqueKeyGeneratorTest {

    private UniqueKeyGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new UniqueKeyGenerator();
    }

    @Test
    void testGenerateUniqueKey() {
        // Given
        int length = 6;

        // When
        String key = generator.generateUniqueKey(length);

        // Then
        assertNotNull(key);
        assertEquals(length, key.length());
        assertTrue(key.matches("[a-zA-Z0-9]+"));
    }

    @Test
    void testGenerateUniqueKey_WithInvalidLength_ThrowsException() {
        // Given
        int invalidLength = -5;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generateUniqueKey(invalidLength)
        );
        assertEquals("Key length must be positive", exception.getMessage());
    }

    @Test
    void testGenerateUniqueKey_GeneratesDifferentKeys() {
        // Given
        int length = 8;

        // When
        String key1 = generator.generateUniqueKey(length);
        String key2 = generator.generateUniqueKey(length);

        // Then - keys should be different (statistically very likely)
        assertNotEquals(key1, key2);
    }
}

