package org.url.shortener.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;
import org.url.shortener.exception.InvalidUrlException;

public class URLValidator {
  
  private static final Pattern URL_PATTERN = Pattern.compile(
      "^(https?://)([\\w\\-]+(\\.[\\w\\-]+)+)(:[0-9]+)?(/.*)?$",
      Pattern.CASE_INSENSITIVE
  );
  
  private static final int MAX_URL_LENGTH = 2048;

  public void validate(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new InvalidUrlException("URL cannot be null or empty");
    }
    
    if (url.length() > MAX_URL_LENGTH) {
      throw new InvalidUrlException("URL exceeds maximum length of " + MAX_URL_LENGTH + " characters");
    }
    
    if (!URL_PATTERN.matcher(url).matches()) {
      throw new InvalidUrlException("Invalid URL format: " + url);
    }
    
    try {
      URL urlObj = new URL(url);
      urlObj.toURI(); // Additional validation
      
      String protocol = urlObj.getProtocol().toLowerCase();
      if (!protocol.equals("http") && !protocol.equals("https")) {
        throw new InvalidUrlException("Only HTTP and HTTPS protocols are supported");
      }
    } catch (MalformedURLException e) {
      throw new InvalidUrlException("Malformed URL: " + url, e);
    } catch (URISyntaxException e) {
      throw new InvalidUrlException("Invalid URI syntax: " + url, e);
    }
  }

  public boolean isValid(String url) {
    try {
      validate(url);
      return true;
    } catch (InvalidUrlException e) {
      return false;
    }
  }
}

