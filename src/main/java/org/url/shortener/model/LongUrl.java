package org.url.shortener.model;

public class LongUrl {
  String url;
  Long expiry;

  public LongUrl(String url, Long expiry) {
    this.url = url;
    this.expiry = expiry;
  }

  public String getUrl() {
    return url;
  }

  public Long getExpiry() {
    return expiry;
  }
}
