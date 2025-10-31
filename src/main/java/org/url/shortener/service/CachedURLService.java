package org.url.shortener.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedURLService implements URLService {
  private final URLService delegate;
  private final Map<String, String> cache = new ConcurrentHashMap<>();

  public CachedURLService(URLService delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getLongUrl(String shortUrl) {
    return cache.computeIfAbsent(shortUrl, delegate::getLongUrl);
  }

  @Override
  public String shortenUrl(String longUrl) {
    String result = delegate.shortenUrl(longUrl);
    cache.put(result, longUrl);
    return result;
  }
}
