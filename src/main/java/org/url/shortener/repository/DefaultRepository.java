package org.url.shortener.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.url.shortener.exception.DuplicateUrlIdentifierException;
import org.url.shortener.model.LongUrl;

@Repository
public class DefaultRepository implements URLRepository {

  private final Map<String, LongUrl> shortToLongMap;
  private final Map<String, String> longToShortMap;
  private final TreeMap<Long, Set<String>> expiryMap;

  public DefaultRepository() {
    this.shortToLongMap = new ConcurrentHashMap<>();
    this.longToShortMap = new ConcurrentHashMap<>();
    this.expiryMap = new TreeMap<>();
  }

  @Override
  public void save(String shortUrlIdentifier, LongUrl longURL) {
    synchronized (expiryMap) {
      if (shortToLongMap.containsKey(shortUrlIdentifier)) {
        throw new DuplicateUrlIdentifierException(
            "URL identifier already exists: " + shortUrlIdentifier);
      }
      shortToLongMap.put(shortUrlIdentifier, longURL);
      longToShortMap.put(longURL.getUrl(), shortUrlIdentifier);
      
      Set<String> urlsAtExpiry = expiryMap.getOrDefault(longURL.getExpiry(), new HashSet<>());
      urlsAtExpiry.add(shortUrlIdentifier);
      expiryMap.put(longURL.getExpiry(), urlsAtExpiry);
    }
  }

  @Override
  public String get(String shortUrlIdentifier) {
    LongUrl longUrl = shortToLongMap.get(shortUrlIdentifier);
    return longUrl != null ? longUrl.getUrl() : null;
  }

  @Override
  public boolean exists(String shortUrlIdentifier) {
    return shortToLongMap.containsKey(shortUrlIdentifier);
  }

  @Override
  public void remove(String shortUrlIdentifier) {
    synchronized (expiryMap) {
      if (!exists(shortUrlIdentifier)) {
        return;
      }
      LongUrl removedUrl = shortToLongMap.remove(shortUrlIdentifier);
      longToShortMap.remove(removedUrl.getUrl());
      
      Set<String> urlsAtExpiry = expiryMap.get(removedUrl.getExpiry());
      if (urlsAtExpiry != null) {
        urlsAtExpiry.remove(shortUrlIdentifier);
        if (urlsAtExpiry.isEmpty()) {
          expiryMap.remove(removedUrl.getExpiry());
        }
      }
    }
  }

  @Override
  public List<String> getAllExpired(long timeStamp) {
    synchronized (expiryMap) {
      List<String> expiredUrls = new ArrayList<>();
      SortedMap<Long, Set<String>> expiredEntries = expiryMap.headMap(timeStamp, true);
      for (Set<String> urls : expiredEntries.values()) {
        expiredUrls.addAll(urls);
      }
      return expiredUrls;
    }
  }
  
  @Override
  public Optional<String> findByLongUrl(String longUrl) {
    return Optional.ofNullable(longToShortMap.get(longUrl));
  }

}
