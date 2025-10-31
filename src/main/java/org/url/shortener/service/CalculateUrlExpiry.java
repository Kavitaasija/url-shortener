package org.url.shortener.service;

import java.time.Instant;


public class CalculateUrlExpiry {
  
  private final long defaultExpirySeconds;

  public CalculateUrlExpiry(long defaultExpirySeconds) {
    if (defaultExpirySeconds <= 0) {
      throw new IllegalArgumentException("Expiry seconds must be positive");
    }
    this.defaultExpirySeconds = defaultExpirySeconds;
  }

  public Long getDefaultExpiry() {
    return Instant.now().plusSeconds(defaultExpirySeconds).getEpochSecond();
  }

  public Long getCustomExpiry(long customExpirySeconds) {
    if (customExpirySeconds <= 0) {
      throw new IllegalArgumentException("Custom expiry seconds must be positive");
    }
    return Instant.now().plusSeconds(customExpirySeconds).getEpochSecond();
  }
}
