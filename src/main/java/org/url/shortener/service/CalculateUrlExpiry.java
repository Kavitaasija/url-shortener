package org.url.shortener.service;

import java.time.Instant;

public class CalculateUrlExpiry {

  Long getDefaultExpiry() {
    return Instant.now().plusSeconds(10).getEpochSecond();
  }
}
