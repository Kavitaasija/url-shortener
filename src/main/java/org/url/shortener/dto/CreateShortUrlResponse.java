package org.url.shortener.dto;

/**
 * Response DTO for creating a short URL.
 */
public class CreateShortUrlResponse {
  
  private String shortUrl;
  private String longUrl;
  private String message;
  
  public CreateShortUrlResponse() {
  }
  
  public CreateShortUrlResponse(String shortUrl, String longUrl, String message) {
    this.shortUrl = shortUrl;
    this.longUrl = longUrl;
    this.message = message;
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
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
}

