package org.url.shortener.repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.url.shortener.exception.DuplicateUrlIdentifierException;
import org.url.shortener.model.LongUrl;

public class DefaultRepository implements URLRepository {

  final Map<String, LongUrl> urls;
  final TreeMap<Long, Set<String>> urlMap;

  public DefaultRepository() {
    urls = new ConcurrentHashMap<>();
    urlMap = new TreeMap<>();
  }

  @Override
  public void save(String shortUrlIdentifier, LongUrl longURL) {
    synchronized (urlMap) {
      if (urls.containsKey(shortUrlIdentifier)) {
        throw new DuplicateUrlIdentifierException("URL identifier already exists: " + shortUrlIdentifier);
      }
      urls.put(shortUrlIdentifier, longURL);
      Set<String> list = urlMap.getOrDefault(longURL.getExpiry(), new HashSet<>());
      list.add(shortUrlIdentifier);
      urlMap.put(longURL.getExpiry(), list);
    }
  }

  @Override
  public String get(String shortUrlIdentifier) {
    return urls.get(shortUrlIdentifier).getUrl();
  }

  @Override
  public boolean exists(String shortUrlIdentifier) {
    return urls.containsKey(shortUrlIdentifier);
  }

  @Override
  public void remove(String shortUrlIdentifier) {
    if (exists(shortUrlIdentifier)) {
      LongUrl removedUrl = urls.remove(shortUrlIdentifier);
      if (urlMap.containsKey(removedUrl.getExpiry())) {
        urlMap.get(removedUrl.getExpiry()).remove(shortUrlIdentifier);
        if (urlMap.get(removedUrl.getExpiry()).isEmpty()) {
          urlMap.remove(removedUrl.getExpiry());
        }
      }
    }
  }

  @Override
  public List<String> getAll(long timeStamp) {
    SortedMap<Long, Set<String>> sortedMap = urlMap.headMap(timeStamp);
    if (sortedMap.isEmpty()) {
      return List.of();
    }
    return sortedMap.values().stream().flatMap(Collection::stream).toList();
  }
}
