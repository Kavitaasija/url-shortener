package org.url.shortener.service;

import java.security.SecureRandom;

public class UniqueKeyGenerator {

  private final static String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  public String generateUniqueKey(int maxLen) {
    SecureRandom random = new SecureRandom();
    StringBuilder uniqueKey = new StringBuilder();
    for (int i = 0; i < maxLen; i++) {
      uniqueKey.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
    }
    return uniqueKey.toString();
  }
}
