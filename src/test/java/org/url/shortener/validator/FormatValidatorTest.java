package org.url.shortener.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.url.shortener.exception.InvalidUrlException;

import static org.junit.jupiter.api.Assertions.*;

class FormatValidatorTest {

    private FormatValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FormatValidator();
    }

    @Test
    void testValidate() {
        // Given
        String validUrl = "https://www.example.com";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> validator.validate(validUrl));
    }

    @Test
    void testValidate_WithInvalidFormat_ThrowsException() {
        // Given
        String invalidUrl = "not-a-valid-url";

        // When & Then
        assertThrows(InvalidUrlException.class, () -> validator.validate(invalidUrl));
    }

    @Test
    void testValidate_WithHttpUrl() {
        // Given
        String httpUrl = "http://www.example.com";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> validator.validate(httpUrl));
    }
}

