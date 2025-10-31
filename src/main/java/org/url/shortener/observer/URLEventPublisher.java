package org.url.shortener.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Centralized event publisher that handles asynchronous notification of URL events.
 * This service decouples event notification from business logic and improves performance.
 */
public class URLEventPublisher {
  
  private final List<URLEventListener> listeners;
  private final ExecutorService executorService;
  private volatile boolean isShutdown = false;
  
  public URLEventPublisher() {
    this(Executors.newFixedThreadPool(3, r -> {
      Thread thread = new Thread(r, "EventPublisher-Thread");
      thread.setDaemon(true);
      return thread;
    }));
  }
  
  public URLEventPublisher(ExecutorService executorService) {
    this.listeners = new ArrayList<>();
    this.executorService = executorService;
  }
  
  /**
   * Registers a listener to receive event notifications.
   */
  public synchronized void subscribe(URLEventListener listener) {
    if (listener != null && !listeners.contains(listener)) {
      listeners.add(listener);
      System.out.println("[EVENT PUBLISHER] Registered listener: " + 
                        listener.getClass().getSimpleName());
    }
  }
  
  /**
   * Unregisters a listener from receiving event notifications.
   */
  public synchronized void unsubscribe(URLEventListener listener) {
    if (listeners.remove(listener)) {
      System.out.println("[EVENT PUBLISHER] Unregistered listener: " + 
                        listener.getClass().getSimpleName());
    }
  }
  
  /**
   * Publishes a URL creation event asynchronously.
   */
  public void publishUrlCreated(String shortUrl, String longUrl, long expiryTime) {
    if (isShutdown) return;
    
    executorService.submit(() -> {
      List<URLEventListener> listenersCopy;
      synchronized (this) {
        listenersCopy = new ArrayList<>(listeners);
      }
      
      for (URLEventListener listener : listenersCopy) {
        try {
          listener.onUrlCreated(shortUrl, longUrl, expiryTime);
        } catch (Exception e) {
          handleListenerError(listener, "onUrlCreated", e);
        }
      }
    });
  }
  
  /**
   * Publishes a URL access event asynchronously.
   */
  public void publishUrlAccessed(String shortUrl, String longUrl) {
    if (isShutdown) return;
    
    executorService.submit(() -> {
      List<URLEventListener> listenersCopy;
      synchronized (this) {
        listenersCopy = new ArrayList<>(listeners);
      }
      
      for (URLEventListener listener : listenersCopy) {
        try {
          listener.onUrlAccessed(shortUrl, longUrl);
        } catch (Exception e) {
          handleListenerError(listener, "onUrlAccessed", e);
        }
      }
    });
  }
  
  /**
   * Publishes a URL deletion event asynchronously.
   */
  public void publishUrlDeleted(String shortUrl) {
    if (isShutdown) return;
    
    executorService.submit(() -> {
      List<URLEventListener> listenersCopy;
      synchronized (this) {
        listenersCopy = new ArrayList<>(listeners);
      }
      
      for (URLEventListener listener : listenersCopy) {
        try {
          listener.onUrlDeleted(shortUrl);
        } catch (Exception e) {
          handleListenerError(listener, "onUrlDeleted", e);
        }
      }
    });
  }
  
  /**
   * Publishes a URL expiration event asynchronously.
   */
  public void publishUrlExpired(String shortUrl) {
    if (isShutdown) return;
    
    executorService.submit(() -> {
      List<URLEventListener> listenersCopy;
      synchronized (this) {
        listenersCopy = new ArrayList<>(listeners);
      }
      
      for (URLEventListener listener : listenersCopy) {
        try {
          listener.onUrlExpired(shortUrl);
        } catch (Exception e) {
          handleListenerError(listener, "onUrlExpired", e);
        }
      }
    });
  }
  
  /**
   * Publishes a collision detection event asynchronously.
   */
  public void publishCollisionDetected(String shortUrl, int attemptNumber) {
    if (isShutdown) return;
    
    executorService.submit(() -> {
      List<URLEventListener> listenersCopy;
      synchronized (this) {
        listenersCopy = new ArrayList<>(listeners);
      }
      
      for (URLEventListener listener : listenersCopy) {
        try {
          listener.onCollisionDetected(shortUrl, attemptNumber);
        } catch (Exception e) {
          handleListenerError(listener, "onCollisionDetected", e);
        }
      }
    });
  }
  
  /**
   * Handles errors that occur when notifying listeners.
   */
  private void handleListenerError(URLEventListener listener, String eventType, Exception e) {
    System.err.println("[EVENT PUBLISHER] Error notifying " + 
                      listener.getClass().getSimpleName() + 
                      " for event: " + eventType + 
                      " - " + e.getMessage());
  }
  
  /**
   * Shuts down the event publisher gracefully.
   */
  public void shutdown() {
    if (isShutdown) return;
    
    isShutdown = true;
    System.out.println("[EVENT PUBLISHER] Shutting down event publisher...");
    
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
        System.err.println("[EVENT PUBLISHER] Forcing shutdown...");
        executorService.shutdownNow();
      }
      System.out.println("[EVENT PUBLISHER] Event publisher shut down successfully");
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
  
  /**
   * Gets the number of registered listeners.
   */
  public synchronized int getListenerCount() {
    return listeners.size();
  }
  
  /**
   * Checks if the publisher has been shut down.
   */
  public boolean isShutdown() {
    return isShutdown;
  }
}

