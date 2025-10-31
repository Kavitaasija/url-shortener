package org.url.shortener.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class URLEventPublisherTest {

    @Mock
    private URLEventListener mockListener;

    private URLEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new URLEventPublisher();
    }

    @Test
    void testSubscribe() {
        // When
        publisher.subscribe(mockListener);

        // Then
        assertEquals(1, publisher.getListenerCount());
    }

    @Test
    void testUnsubscribe() {
        // Given
        publisher.subscribe(mockListener);

        // When
        publisher.unsubscribe(mockListener);

        // Then
        assertEquals(0, publisher.getListenerCount());
    }

    @Test
    void testPublishUrlCreated() throws InterruptedException {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        long expiryTime = 3600L;
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockListener).onUrlCreated(anyString(), anyString(), anyLong());

        publisher.subscribe(mockListener);

        // When
        publisher.publishUrlCreated(shortUrl, longUrl, expiryTime);

        // Then
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        verify(mockListener, timeout(1000)).onUrlCreated(shortUrl, longUrl, expiryTime);
    }

    @Test
    void testPublishUrlAccessed() throws InterruptedException {
        // Given
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockListener).onUrlAccessed(anyString(), anyString());

        publisher.subscribe(mockListener);

        // When
        publisher.publishUrlAccessed(shortUrl, longUrl);

        // Then
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        verify(mockListener, timeout(1000)).onUrlAccessed(shortUrl, longUrl);
    }

    @Test
    void testShutdown() {
        // When
        publisher.shutdown();

        // Then
        assertTrue(publisher.isShutdown());
    }

    @Test
    void testPublishAfterShutdown_DoesNotNotifyListeners() throws InterruptedException {
        // Given
        publisher.subscribe(mockListener);
        publisher.shutdown();
        Thread.sleep(100); // Wait for shutdown to complete

        // When
        publisher.publishUrlCreated("test", "https://test.com", 3600L);
        Thread.sleep(100); // Give time for potential async execution

        // Then
        verify(mockListener, never()).onUrlCreated(anyString(), anyString(), anyLong());
    }
}

