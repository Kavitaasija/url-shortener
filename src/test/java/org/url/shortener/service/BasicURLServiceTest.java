package org.url.shortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.url.shortener.config.URLShortenerConfig;
import org.url.shortener.exception.InvalidUrlException;
import org.url.shortener.exception.MaxAttemptReachedException;
import org.url.shortener.exception.NotFoundException;
import org.url.shortener.model.LongUrl;
import org.url.shortener.observer.URLEventPublisher;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.strategy.StrategyFactory;
import org.url.shortener.strategy.URLGenerationStrategy;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicURLServiceTest {

    @Mock
    private URLRepository mockRepository;

    @Mock
    private URLGenerationStrategy mockStrategy;

    @Mock
    private StrategyFactory strategyFactory;

    @Mock
    private URLEventPublisher mockEventPublisher;

    private BasicURLService urlService;
    private URLShortenerConfig config;

    @BeforeEach
    void setUp() {
        config = new URLShortenerConfig();
        urlService = new BasicURLService(mockRepository, strategyFactory, config, mockEventPublisher);

    }

    @Test
    void testShortenUrl() {
        // Given
        String longUrl = "https://www.example.com";
        String shortKey = "abc123";
        when(strategyFactory.getDefaultStrategy()).thenReturn(mockStrategy);
        when(mockStrategy.generateUniqueKey(eq(longUrl), anyInt())).thenReturn(shortKey);
        when(mockRepository.findByLongUrl(longUrl)).thenReturn(Optional.empty());
        doNothing().when(mockRepository).save(eq(shortKey), any(LongUrl.class));

        // When
        String result = urlService.shortenUrl(longUrl);

        // Then
        assertEquals(shortKey, result);
        verify(mockRepository).save(eq(shortKey), any(LongUrl.class));
        verify(mockEventPublisher).publishUrlCreated(eq(shortKey), eq(longUrl), anyLong());
    }

    @Test
    void testShortenUrl_WithInvalidUrl_ThrowsException() {
        // Given
        String invalidUrl = "not-a-valid-url";

        // When & Then
        assertThrows(InvalidUrlException.class, () -> urlService.shortenUrl(invalidUrl));
    }

    @Test
    void testShortenUrl_ReturnsDuplicateShortUrl() {
        // Given
        String longUrl = "https://www.example.com";
        String existingShortUrl = "existing";
        when(mockRepository.findByLongUrl(longUrl)).thenReturn(Optional.of(existingShortUrl));

        // When
        String result = urlService.shortenUrl(longUrl);

        // Then
        assertEquals(existingShortUrl, result);
        verify(mockRepository, never()).save(anyString(), any(LongUrl.class));
    }

    @Test
    void testGetLongUrl() {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        when(mockRepository.get(shortUrl)).thenReturn(longUrl);

        // When
        String result = urlService.getLongUrl(shortUrl);

        // Then
        assertEquals(longUrl, result);
        verify(mockEventPublisher).publishUrlAccessed(shortUrl, longUrl);
    }

    @Test
    void testGetLongUrl_NotFound_ThrowsException() {
        // Given
        String shortUrl = "nonexistent";
        when(mockRepository.get(shortUrl)).thenReturn(null);

        // When & Then
        assertThrows(NotFoundException.class, () -> urlService.getLongUrl(shortUrl));
    }

    @Test
    void testDeleteUrl() {
        // Given
        String shortUrl = "abc123";
        when(mockRepository.exists(shortUrl)).thenReturn(true);
        doNothing().when(mockRepository).remove(shortUrl);

        // When
        boolean result = urlService.deleteUrl(shortUrl);

        // Then
        assertTrue(result);
        verify(mockRepository).remove(shortUrl);
        verify(mockEventPublisher).publishUrlDeleted(shortUrl);
    }

    @Test
    void testDeleteUrl_NotFound() {
        // Given
        String shortUrl = "nonexistent";
        when(mockRepository.exists(shortUrl)).thenReturn(false);

        // When
        boolean result = urlService.deleteUrl(shortUrl);

        // Then
        assertFalse(result);
        verify(mockRepository, never()).remove(anyString());
    }
}

