package org.url.shortener.exception;

public class ResourceException extends RuntimeException {
  public ResourceException(String url) {
    super("URL " + url);
  }
}
