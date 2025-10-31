package org.url.shortener.strategy;

public interface URLGenerationStrategy {
    String generateUniqueKey(String longUrl, int length);
}
