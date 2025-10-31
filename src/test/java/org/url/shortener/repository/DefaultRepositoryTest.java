package org.url.shortener.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.url.shortener.exception.DuplicateUrlIdentifierException;
import org.url.shortener.model.LongUrl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DefaultRepositoryTest {

    private DefaultRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DefaultRepository();
    }

    @Test
    void testSaveAndGet() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        Long expiry = 3600L;
        LongUrl longUrlObj = new LongUrl(longUrl, expiry);

        // When
        repository.save(shortUrl, longUrlObj);
        String retrievedUrl = repository.get(shortUrl);

        // Then
        assertEquals(longUrl, retrievedUrl);
        assertTrue(repository.exists(shortUrl));
    }

    @Test
    void testSave_DuplicateIdentifier_ThrowsException() {
        // Given
        String shortUrl = "abc123";
        LongUrl longUrl1 = new LongUrl("https://www.example.com", 3600L);
        LongUrl longUrl2 = new LongUrl("https://www.google.com", 3600L);
        repository.save(shortUrl, longUrl1);

        // When & Then
        assertThrows(DuplicateUrlIdentifierException.class, 
            () -> repository.save(shortUrl, longUrl2));
    }

    @Test
    void testRemove() {
        // Given
        String shortUrl = "abc123";
        LongUrl longUrlObj = new LongUrl("https://www.example.com", 3600L);
        repository.save(shortUrl, longUrlObj);

        // When
        repository.remove(shortUrl);

        // Then
        assertFalse(repository.exists(shortUrl));
        assertNull(repository.get(shortUrl));
    }

    @Test
    void testGetAllExpired() {
        // Given
        long currentTime = System.currentTimeMillis() / 1000;
        String expiredUrl1 = "exp1";
        String expiredUrl2 = "exp2";
        String validUrl = "valid";
        
        repository.save(expiredUrl1, new LongUrl("https://example1.com", currentTime - 100));
        repository.save(expiredUrl2, new LongUrl("https://example2.com", currentTime - 50));
        repository.save(validUrl, new LongUrl("https://valid.com", currentTime + 1000));

        // When
        List<String> expiredUrls = repository.getAllExpired(currentTime);

        // Then
        assertEquals(2, expiredUrls.size());
        assertTrue(expiredUrls.contains(expiredUrl1));
        assertTrue(expiredUrls.contains(expiredUrl2));
        assertFalse(expiredUrls.contains(validUrl));
    }

    @Test
    void testFindByLongUrl() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        repository.save(shortUrl, new LongUrl(longUrl, 3600L));

        // When
        Optional<String> result = repository.findByLongUrl(longUrl);

        // Then
        assertTrue(result.isPresent());
        assertEquals(shortUrl, result.get());
    }

    @Test
    void testFindByLongUrl_NotFound() {
        // Given
        String nonExistentUrl = "https://nonexistent.com";

        // When
        Optional<String> result = repository.findByLongUrl(nonExistentUrl);

        // Then
        assertFalse(result.isPresent());
    }
}

