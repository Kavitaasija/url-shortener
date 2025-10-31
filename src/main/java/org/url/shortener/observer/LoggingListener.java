package org.url.shortener.observer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LoggingListener implements URLEventListener {
  
  private static final DateTimeFormatter DATE_FORMATTER = 
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                      .withZone(ZoneId.systemDefault());
  
  @Override
  public void onUrlCreated(String shortUrl, String longUrl, long expiryTime) {
    String expiryDate = DATE_FORMATTER.format(Instant.ofEpochSecond(expiryTime));
    System.out.println("[LOG] URL Created: " + shortUrl + " -> " + longUrl + 
                      " (expires: " + expiryDate + ")");
  }
  
  @Override
  public void onUrlAccessed(String shortUrl, String longUrl) {
    System.out.println("[LOG] URL Accessed: " + shortUrl + " resolved to " + longUrl);
  }
  
  @Override
  public void onUrlDeleted(String shortUrl) {
    System.out.println("[LOG] URL Deleted: " + shortUrl);
  }
  
  @Override
  public void onUrlExpired(String shortUrl) {
    System.out.println("[LOG] URL Expired: " + shortUrl + " (automatically removed)");
  }
  
  @Override
  public void onCollisionDetected(String shortUrl, int attemptNumber) {
    System.out.println("[LOG] Collision detected for: " + shortUrl + 
                      " (attempt #" + attemptNumber + ")");
  }
}

