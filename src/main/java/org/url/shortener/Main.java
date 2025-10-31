package org.url.shortener;

import org.url.shortener.config.URLShortenerConfig;
import org.url.shortener.repository.DefaultRepository;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.scheduler.DeleteExpiryUrlsScheduler;
import org.url.shortener.scheduler.SchedulerConfig;
import org.url.shortener.service.URLService;
import org.url.shortener.service.UniqueKeyGenerator;

public class Main {
  
  public static void main(String[] args) throws InterruptedException {
    System.out.println("=== URL Shortener Service Starting ===\n");

    URLShortenerConfig config = new URLShortenerConfig();
    
    URLRepository urlRepository = new DefaultRepository();
    
    SchedulerConfig schedulerConfig = new SchedulerConfig(
        config.getCleanupFrequencySeconds(),
        config.getSchedulerThreadPoolSize()
    );
    DeleteExpiryUrlsScheduler scheduler = new DeleteExpiryUrlsScheduler(
        urlRepository,
        schedulerConfig
    );
    scheduler.start();
    
    URLService urlService = new URLService(
        urlRepository,
        new UniqueKeyGenerator(),
        config
    );
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("\n=== Shutting down URL Shortener Service ===");
      scheduler.shutdown();
    }));

    demonstrateURLShortener(urlService);
  }
  
  /**
   * Demonstrates various URL shortener operations.
   */
  private static void demonstrateURLShortener(URLService urlService) throws InterruptedException {
    System.out.println("--- Demo: Creating Short URLs ---\n");
    
    String longUrl1 = "https://www.google.com/search?q=java";
    String shortUrl1 = urlService.shortenUrl(longUrl1);
    System.out.println("Original: " + longUrl1);
    System.out.println("Shortened: " + shortUrl1);
    System.out.println("Retrieved: " + urlService.getLongUrl(shortUrl1));
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
    
    for (String url : testUrls) {
      String shortUrl = urlService.shortenUrl(url);
      System.out.println("Short: " + shortUrl + " -> " + url);
    }
    

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
    
    System.out.println("\n=== Demo Complete ===");
    System.out.println("Service will continue running. Press Ctrl+C to stop.");
    
  }
}