package org.url.shortener.model;

public class LongUrl {
  private final String url;
  private final Long expiry;

  public LongUrl(String url, Long expiry) {
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalArgumentException("URL cannot be null or empty");
    }
    if (expiry == null || expiry <= 0) {
      throw new IllegalArgumentException("Expiry must be a positive timestamp");
    }
    this.url = url;
    this.expiry = expiry;
  }

  public String getUrl() {
    return url;
  }

  public Long getExpiry() {
    return expiry;
  }
  
  @Override
  public String toString() {
    return "LongUrl{url='" + url + "', expiry=" + expiry + "}";
  }
}
