package org.url.shortener.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.url.shortener.exception.InvalidUrlException;

import static org.junit.jupiter.api.Assertions.*;

class NullCheckValidatorTest {

    private NullCheckValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NullCheckValidator();
    }

    @Test
    void testValidate() {
        // Given
        String validUrl = "https://www.example.com";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> validator.validate(validUrl));
    }

    @Test
    void testValidate_WithWhitespaceUrl_ThrowsException() {
        // Given
        String whitespaceUrl = "   ";

        // When & Then
        assertThrows(InvalidUrlException.class, () -> validator.validate(whitespaceUrl));
    }
}

