package org.url.shortener.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.url.shortener.exception.InvalidUrlException;

import static org.junit.jupiter.api.Assertions.*;

class LengthValidatorTest {

    private LengthValidator validator;

    @BeforeEach
    void setUp() {
        validator = new LengthValidator();
    }

    @Test
    void testValidate() {
        // Given
        String validUrl = "https://www.example.com";

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> validator.validate(validUrl));
    }

    @Test
    void testValidate_WithUrlExceedingMaxLength_ThrowsException() {
        // Given
        StringBuilder longUrl = new StringBuilder("https://www.example.com/");
        while (longUrl.length() <= LengthValidator.MAX_URL_LENGTH) {
            longUrl.append("a");
        }

        // When & Then
        assertThrows(InvalidUrlException.class, () -> validator.validate(longUrl.toString()));
    }
}

