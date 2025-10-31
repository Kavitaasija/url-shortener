package org.url.shortener.exception;

public class DuplicateUrlIdentifierException extends RuntimeException {
  public DuplicateUrlIdentifierException(String message) {
    super(message);
  }
}
