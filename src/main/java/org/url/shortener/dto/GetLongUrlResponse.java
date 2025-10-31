package org.url.shortener.dto;

/**
 * Response DTO for retrieving a long URL.
 */
public class GetLongUrlResponse {
  
  private String shortUrl;
  private String longUrl;
  
  public GetLongUrlResponse() {
  }
  
  public GetLongUrlResponse(String shortUrl, String longUrl) {
    this.shortUrl = shortUrl;
    this.longUrl = longUrl;
  }
  
  public String getShortUrl() {
    return shortUrl;
  }
  
  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }
  
  public String getLongUrl() {
    return longUrl;
  }
  
  public void setLongUrl(String longUrl) {
    this.longUrl = longUrl;
  }
}

