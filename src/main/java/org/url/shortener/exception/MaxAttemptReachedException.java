package org.url.shortener.exception;

public class MaxAttemptReachedException extends RuntimeException {
  public MaxAttemptReachedException(String message) {
    super(message);
  }
}
