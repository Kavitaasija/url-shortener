package org.url.shortener.observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener that collects analytics data about URL usage.
 * Tracks access patterns and popular URLs.
 */
public class AnalyticsListener implements URLEventListener {
  
  private final Map<String, AtomicInteger> accessCounts = new ConcurrentHashMap<>();
  private final Map<String, Long> creationTimes = new ConcurrentHashMap<>();
  
  @Override
  public void onUrlCreated(String shortUrl, String longUrl, long expiryTime) {
    creationTimes.put(shortUrl, System.currentTimeMillis());
    accessCounts.put(shortUrl, new AtomicInteger(0));
    System.out.println("[ANALYTICS] Tracking new URL: " + shortUrl);
  }
  
  @Override
  public void onUrlAccessed(String shortUrl, String longUrl) {
    AtomicInteger count = accessCounts.get(shortUrl);
    if (count != null) {
      int newCount = count.incrementAndGet();
      System.out.println("[ANALYTICS] " + shortUrl + " accessed " + newCount + " time(s)");
      
      // Alert on popular URLs
      if (newCount == 10) {
        System.out.println("[ANALYTICS] 🔥 POPULAR: " + shortUrl + " reached 10 accesses!");
      }
    }
  }
  
  @Override
  public void onUrlDeleted(String shortUrl) {
    Integer count = accessCounts.containsKey(shortUrl) 
        ? accessCounts.get(shortUrl).get() : 0;
    System.out.println("[ANALYTICS] URL " + shortUrl + " deleted after " + 
                      count + " access(es)");
    cleanupUrlData(shortUrl);
  }
  
  @Override
  public void onUrlExpired(String shortUrl) {
    Integer count = accessCounts.containsKey(shortUrl) 
        ? accessCounts.get(shortUrl).get() : 0;
    Long createdTime = creationTimes.get(shortUrl);
    
    if (createdTime != null) {
      long lifespan = System.currentTimeMillis() - createdTime;
      System.out.println("[ANALYTICS] URL " + shortUrl + " expired after " + 
                        (lifespan / 1000) + " seconds with " + count + " access(es)");
    }
    cleanupUrlData(shortUrl);
  }
  
  @Override
  public void onCollisionDetected(String shortUrl, int attemptNumber) {
    // Analytics doesn't track collisions, but could be extended to do so
  }
  
  /**
   * Removes tracking data for a URL.
   */
  private void cleanupUrlData(String shortUrl) {
    accessCounts.remove(shortUrl);
    creationTimes.remove(shortUrl);
  }
  
  /**
   * Gets the most accessed URLs.
   */
  public void printTopUrls(int limit) {
    System.out.println("\n=== TOP " + limit + " MOST ACCESSED URLs ===");
    accessCounts.entrySet().stream()
        .sorted((e1, e2) -> e2.getValue().get() - e1.getValue().get())
        .limit(limit)
        .forEach(entry -> 
            System.out.println(entry.getKey() + ": " + entry.getValue().get() + " accesses")
        );
    System.out.println("=====================================\n");
  }
  
  /**
   * Gets the access count for a specific URL.
   */
  public int getAccessCount(String shortUrl) {
    AtomicInteger count = accessCounts.get(shortUrl);
    return count != null ? count.get() : 0;
  }
}

