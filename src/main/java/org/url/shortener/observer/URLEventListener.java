package org.url.shortener.observer;


public interface URLEventListener {

  void onUrlCreated(String shortUrl, String longUrl, long expiryTime);

  void onUrlAccessed(String shortUrl, String longUrl);

  void onUrlDeleted(String shortUrl);

  void onUrlExpired(String shortUrl);

  void onCollisionDetected(String shortUrl, int attemptNumber);
}

