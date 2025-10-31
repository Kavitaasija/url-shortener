package org.url.shortener.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.url.shortener.exception.InvalidUrlException;

import static org.junit.jupiter.api.Assertions.*;

class AdditionalValidatorTest {

    private AdditionalValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AdditionalValidator();
    }

    @Test
    void testValidate() {
        // Given
        String validUrl = "https://www.example.com";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> validator.validate(validUrl));
    }

    @Test
    void testValidate_WithUnsupportedProtocol_ThrowsException() {
        // Given
        String ftpUrl = "ftp://www.example.com";

        // When & Then
        assertThrows(InvalidUrlException.class, () -> validator.validate(ftpUrl));
    }

    @Test
    void testValidate_WithMalformedUrl_ThrowsException() {
        // Given
        String malformedUrl = "https://";

        // When & Then
        assertThrows(InvalidUrlException.class, () -> validator.validate(malformedUrl));
    }
}

