package org.url.shortener.repository;

import java.util.List;
import org.url.shortener.model.LongUrl;

public interface URLRepository {
  void save(String shortUrlIdentifier, LongUrl longURL);
  String get(String shortUrlIdentifier);
  boolean exists(String shortUrlIdentifier);
  void remove(String shortUrlIdentifier);
  List<String> getAll(long timeStamp);
}
