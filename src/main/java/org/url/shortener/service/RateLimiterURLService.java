package org.url.shortener.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.url.shortener.exception.ResourceException;

public class RateLimiterURLService implements URLService {
  private final URLService delegate;
  private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();

  public RateLimiterURLService(URLService delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getLongUrl(String shortUrl) {
    rateLimter(shortUrl);
    requestCounts.put(shortUrl, requestCounts.getOrDefault(shortUrl, 0) + 1);
    return delegate.getLongUrl(shortUrl);
  }

  @Override
  public String shortenUrl(String longUrl) {
    return delegate.shortenUrl(longUrl);
  }

  private void rateLimter(String shortUrl) {
    int maxRequestsPerMinute = 100;
    if(requestCounts.get(shortUrl) >= maxRequestsPerMinute) {
      throw new ResourceException("too many call for resource ");
    }
  }
}
