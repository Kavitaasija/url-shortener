package org.url.shortener.observer;

import java.util.concurrent.atomic.AtomicLong;


public class MetricsListener implements URLEventListener {
  
  private final AtomicLong urlsCreated = new AtomicLong(0);
  private final AtomicLong urlsAccessed = new AtomicLong(0);
  private final AtomicLong urlsDeleted = new AtomicLong(0);
  private final AtomicLong urlsExpired = new AtomicLong(0);
  private final AtomicLong collisions = new AtomicLong(0);
  
  @Override
  public void onUrlCreated(String shortUrl, String longUrl, long expiryTime) {
    long count = urlsCreated.incrementAndGet();
    System.out.println("[METRICS] Total URLs created: " + count);
  }
  
  @Override
  public void onUrlAccessed(String shortUrl, String longUrl) {
    long count = urlsAccessed.incrementAndGet();
    if (count % 10 == 0) { // Log every 10 accesses
      System.out.println("[METRICS] Total URL accesses: " + count);
    }
  }
  
  @Override
  public void onUrlDeleted(String shortUrl) {
    long count = urlsDeleted.incrementAndGet();
    System.out.println("[METRICS] Total URLs deleted: " + count);
  }
  
  @Override
  public void onUrlExpired(String shortUrl) {
    long count = urlsExpired.incrementAndGet();
    System.out.println("[METRICS] Total URLs expired: " + count);
  }
  
  @Override
  public void onCollisionDetected(String shortUrl, int attemptNumber) {
    long count = collisions.incrementAndGet();
    System.out.println("[METRICS] Total collisions encountered: " + count);
  }

  public void printSummary() {
    System.out.println("\n=== METRICS SUMMARY ===");
    System.out.println("URLs Created:    " + urlsCreated.get());
    System.out.println("URLs Accessed:   " + urlsAccessed.get());
    System.out.println("URLs Deleted:    " + urlsDeleted.get());
    System.out.println("URLs Expired:    " + urlsExpired.get());
    System.out.println("Collisions:      " + collisions.get());
    System.out.println("======================\n");
  }
  
}

