package org.url.shortener.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateShortUrlRequest {
  
  @NotBlank(message = "Long URL is required")
  private String longUrl;
  
  public CreateShortUrlRequest() {
  }
  
  public CreateShortUrlRequest(String longUrl) {
    this.longUrl = longUrl;
  }
  
  public String getLongUrl() {
    return longUrl;
  }
  
  public void setLongUrl(String longUrl) {
    this.longUrl = longUrl;
  }
}

