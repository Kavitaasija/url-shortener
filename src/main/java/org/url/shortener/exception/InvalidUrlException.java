package org.url.shortener.exception;

/**
 * Exception thrown when an invalid URL format is provided.
 */
public class InvalidUrlException extends RuntimeException {
  public InvalidUrlException(String message) {
    super(message);
  }
  
  public InvalidUrlException(String message, Throwable cause) {
    super(message, cause);
  }
}

