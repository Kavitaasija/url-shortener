package org.url.shortener.scheduler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerConfigTest {

    @Test
    void testCreateSchedulerConfig() {
        // Given
        Integer frequency = 60;
        Integer threadPoolSize = 2;

        // When
        SchedulerConfig config = new SchedulerConfig(frequency, threadPoolSize);

        // Then
        assertEquals(frequency, config.getFrequency());
        assertEquals(threadPoolSize, config.getThreadPoolSize());
    }

    @Test
    void testCreateSchedulerConfig_WithInvalidFrequency_ThrowsException() {
        // Given
        Integer invalidFrequency = -10;
        Integer threadPoolSize = 2;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SchedulerConfig(invalidFrequency, threadPoolSize)
        );
        assertEquals("Frequency must be positive", exception.getMessage());
    }

    @Test
    void testCreateSchedulerConfig_WithInvalidThreadPoolSize_ThrowsException() {
        // Given
        Integer frequency = 60;
        Integer invalidThreadPoolSize = 0;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SchedulerConfig(frequency, invalidThreadPoolSize)
        );
        assertEquals("Thread pool size must be positive", exception.getMessage());
    }
}

