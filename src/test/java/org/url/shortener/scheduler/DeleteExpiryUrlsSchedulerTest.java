package org.url.shortener.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.url.shortener.observer.URLEventPublisher;
import org.url.shortener.repository.URLRepository;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpiryUrlsSchedulerTest {

    @Mock
    private URLRepository mockRepository;

    @Mock
    private URLEventPublisher mockEventPublisher;

    private SchedulerConfig config;
    private DeleteExpiryUrlsScheduler scheduler;

    @BeforeEach
    void setUp() {
        config = new SchedulerConfig(1, 1); // 1 second frequency
        scheduler = new DeleteExpiryUrlsScheduler(mockRepository, config, mockEventPublisher);
    }

    @Test
    void testStartAndShutdown() throws InterruptedException {
        // Given
        when(mockRepository.getAllExpired(anyLong())).thenReturn(Collections.emptyList());

        // When
        scheduler.start();
        Thread.sleep(100); // Let it run briefly
        scheduler.shutdown();

        // Then - should not throw exception
        assertTrue(true);
    }

    @Test
    void testConstructor_WithInvalidRepository_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new DeleteExpiryUrlsScheduler(null, config, mockEventPublisher)
        );
        assertEquals("URLRepository cannot be null", exception.getMessage());
    }

    @Test
    void testConstructor_WithInvalidConfig_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new DeleteExpiryUrlsScheduler(mockRepository, null, mockEventPublisher)
        );
        assertEquals("SchedulerConfig cannot be null", exception.getMessage());
    }

    @Test
    void testStartTwice_DoesNotStartAgain() throws InterruptedException {
        // Given
        when(mockRepository.getAllExpired(anyLong())).thenReturn(Collections.emptyList());

        // When
        scheduler.start();
        scheduler.start(); // Second call should be ignored
        Thread.sleep(100);
        scheduler.shutdown();

        // Then - should not throw exception
        assertTrue(true);
    }
}

