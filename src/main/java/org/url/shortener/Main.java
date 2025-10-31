package org.url.shortener;

import org.url.shortener.config.URLShortenerConfig;
import org.url.shortener.observer.AnalyticsListener;
import org.url.shortener.observer.LoggingListener;
import org.url.shortener.observer.MetricsListener;
import org.url.shortener.observer.URLEventPublisher;
import org.url.shortener.repository.DefaultRepository;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.scheduler.DeleteExpiryUrlsScheduler;
import org.url.shortener.scheduler.SchedulerConfig;
import org.url.shortener.service.UniqueKeyGenerator;
import org.url.shortener.service.URLService;
import org.url.shortener.strategy.RandomGenerationStrategy;

public class Main {
  
  public static void main(String[] args) throws InterruptedException {
    System.out.println("=== URL Shortener Service Starting ===\n");
    System.out.println("🎯 Observer Pattern: Event listeners with ASYNC notification!\n");

    URLShortenerConfig config = new URLShortenerConfig();
    
    URLRepository urlRepository = new DefaultRepository();
    
    // Create centralized event publisher for async notifications
    URLEventPublisher eventPublisher = new URLEventPublisher();
    System.out.println();
    
    // Create Observer Pattern listeners
    LoggingListener loggingListener = new LoggingListener();
    MetricsListener metricsListener = new MetricsListener();
    AnalyticsListener analyticsListener = new AnalyticsListener();
    
    // Subscribe listeners to the event publisher
    eventPublisher.subscribe(loggingListener);
    eventPublisher.subscribe(metricsListener);
    eventPublisher.subscribe(analyticsListener);
    System.out.println();
    
    // Create URLService with event publisher
    URLService urlService = new URLService(
        urlRepository,
        new RandomGenerationStrategy(new UniqueKeyGenerator()),
        config,
        eventPublisher
    );
    
    // Create scheduler with event publisher
    SchedulerConfig schedulerConfig = new SchedulerConfig(
        config.getCleanupFrequencySeconds(),
        config.getSchedulerThreadPoolSize()
    );
    DeleteExpiryUrlsScheduler scheduler = new DeleteExpiryUrlsScheduler(
        urlRepository,
        schedulerConfig,
        eventPublisher
    );
    
    scheduler.start();
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("\n=== Shutting down URL Shortener Service ===");
      scheduler.shutdown();
      
      // Give async events time to complete
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      
      metricsListener.printSummary();
      analyticsListener.printTopUrls(5);
      eventPublisher.shutdown();
    }));

    demonstrateURLShortener(urlService, metricsListener, analyticsListener);
  }
  
  /**
   * Demonstrates various URL shortener operations with Observer Pattern.
   */
  private static void demonstrateURLShortener(URLService urlService, 
                                              MetricsListener metricsListener,
                                              AnalyticsListener analyticsListener) throws InterruptedException {
    System.out.println("--- Demo: Creating Short URLs ---\n");
    
    String longUrl1 = "https://www.google.com/search?q=java";
    String shortUrl1 = urlService.shortenUrl(longUrl1);
    System.out.println("Original: " + longUrl1);
    System.out.println("Shortened: " + shortUrl1);
    System.out.println();
    
    // Access the URL multiple times to demonstrate analytics
    System.out.println("\n--- Demo: Accessing URLs (Observer Pattern in Action) ---\n");
    for (int i = 0; i < 3; i++) {
      String retrieved = urlService.getLongUrl(shortUrl1);
      System.out.println("Retrieved: " + retrieved);
    }
    System.out.println();
    
    String shortUrl1Duplicate = urlService.shortenUrl(longUrl1);
    System.out.println("Duplicate check - Same short URL returned: " + shortUrl1.equals(shortUrl1Duplicate));
    System.out.println();
    
    String[] testUrls = {
        "https://github.com/explore",
        "https://stackoverflow.com/questions",
        "https://www.youtube.com/watch?v=example",
        "https://www.amazon.com/products"
    };
    
    System.out.println("\n--- Creating Multiple URLs ---\n");
    String[] shortUrls = new String[testUrls.length];
    for (int i = 0; i < testUrls.length; i++) {
      String url = testUrls[i];
      shortUrls[i] = urlService.shortenUrl(url);
      System.out.println("Short: " + shortUrls[i] + " -> " + url);
    }
    
    // Access URLs with different frequencies to show analytics
    System.out.println("\n--- Demo: Multiple Accesses for Analytics ---\n");
    for (int i = 0; i < 7; i++) {
      urlService.getLongUrl(shortUrls[0]); // Access first URL 7 times
    }
    for (int i = 0; i < 5; i++) {
      urlService.getLongUrl(shortUrls[1]); // Access second URL 5 times
    }
    urlService.getLongUrl(shortUrls[2]); // Access third URL once

    System.out.println("\n--- Testing URL Deletion ---");
    boolean deleted = urlService.deleteUrl(shortUrls[3]);
    System.out.println("Deletion successful: " + deleted);
    
    System.out.println("\n--- Testing Invalid URL ---");
    try {
      urlService.shortenUrl("not-a-valid-url");
    } catch (Exception e) {
      System.out.println("Caught expected exception: " + e.getMessage());
    }
    
    System.out.println("\n--- Testing Non-existent Short URL ---");
    try {
      urlService.getLongUrl("NOTEXIST");
    } catch (Exception e) {
      System.out.println("Caught expected exception: " + e.getMessage());
    }
    
    // Print current analytics
    System.out.println("\n--- Current Analytics ---");
    analyticsListener.printTopUrls(3);
    
    System.out.println("\n=== Demo Complete ===");
    System.out.println("Service will continue running. Press Ctrl+C to stop.");
    System.out.println("Note: URLs will expire after " + 
                      (new URLShortenerConfig().getDefaultUrlExpirySeconds() / 60) + 
                      " minute(s)");
    System.out.println("Scheduler will clean them up and notify observers.\n");
    
  }
}