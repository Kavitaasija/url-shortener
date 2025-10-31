package org.url.shortener.config;

/**
 * Configuration class for URL Shortener settings.
 * All configuration values are centralized here for easy modification.
 */
public class URLShortenerConfig {
  
  // URL Generation Settings
  private final int shortUrlLength;
  private final int maxCollisionRetryAttempts;
  
  // Expiry Settings (in seconds)
  private final long defaultUrlExpirySeconds;
  
  // Scheduler Settings
  private final int cleanupFrequencySeconds;
  private final int schedulerThreadPoolSize;
  
  // Validation Settings
  private final boolean enableUrlValidation;
  private final boolean preventDuplicateLongUrls;
  
  /**
   * Creates a configuration with default values.
   */
  public URLShortenerConfig() {
    this.shortUrlLength = 6;
    this.maxCollisionRetryAttempts = 5;
    this.defaultUrlExpirySeconds = 3600; // 1 hour
    this.cleanupFrequencySeconds = 60; // 1 minute
    this.schedulerThreadPoolSize = 2;
    this.enableUrlValidation = true;
    this.preventDuplicateLongUrls = true;
  }
  
  /**
   * Creates a custom configuration.
   */
  public URLShortenerConfig(int shortUrlLength, int maxCollisionRetryAttempts,
                            long defaultUrlExpirySeconds, int cleanupFrequencySeconds,
                            int schedulerThreadPoolSize, boolean enableUrlValidation,
                            boolean preventDuplicateLongUrls) {
    this.shortUrlLength = shortUrlLength;
    this.maxCollisionRetryAttempts = maxCollisionRetryAttempts;
    this.defaultUrlExpirySeconds = defaultUrlExpirySeconds;
    this.cleanupFrequencySeconds = cleanupFrequencySeconds;
    this.schedulerThreadPoolSize = schedulerThreadPoolSize;
    this.enableUrlValidation = enableUrlValidation;
    this.preventDuplicateLongUrls = preventDuplicateLongUrls;
  }
  
  public int getShortUrlLength() {
    return shortUrlLength;
  }
  
  public int getMaxCollisionRetryAttempts() {
    return maxCollisionRetryAttempts;
  }
  
  public long getDefaultUrlExpirySeconds() {
    return defaultUrlExpirySeconds;
  }
  
  public int getCleanupFrequencySeconds() {
    return cleanupFrequencySeconds;
  }
  
  public int getSchedulerThreadPoolSize() {
    return schedulerThreadPoolSize;
  }
  
  public boolean isUrlValidationEnabled() {
    return enableUrlValidation;
  }
  
  public boolean isDuplicateLongUrlsPrevented() {
    return preventDuplicateLongUrls;
  }
}

