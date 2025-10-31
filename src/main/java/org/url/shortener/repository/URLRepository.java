package org.url.shortener.repository;

import java.util.List;
import java.util.Optional;
import org.url.shortener.model.LongUrl;

public interface URLRepository {
  void save(String shortUrlIdentifier, LongUrl longURL);
  String get(String shortUrlIdentifier);
  boolean exists(String shortUrlIdentifier);
  void remove(String shortUrlIdentifier);
  List<String> getAllExpired(long timeStamp);
  Optional<String> findByLongUrl(String longUrl);
}
