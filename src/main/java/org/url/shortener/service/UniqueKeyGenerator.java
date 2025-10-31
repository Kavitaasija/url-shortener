package org.url.shortener.service;

import java.security.SecureRandom;


public class UniqueKeyGenerator {

  private static final String ALLOWED_CHARS = 
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  
  private final SecureRandom random;

  public UniqueKeyGenerator() {
    this.random = new SecureRandom();
  }

  public String generateUniqueKey(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Key length must be positive");
    }
    
    StringBuilder uniqueKey = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      uniqueKey.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
    }
    return uniqueKey.toString();
  }
}
