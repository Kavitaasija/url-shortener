package org.url.shortener.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String urlIdentifier) {
    super("Not found: " + urlIdentifier);
  }
}
