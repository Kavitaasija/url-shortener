package org.url.shortener.validator;

import org.url.shortener.exception.InvalidUrlException;

public class NullCheckValidator extends URLValidator {

  @Override
  protected  boolean doValidate(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new InvalidUrlException("URL cannot be null or empty");
    }
    return true;
  }
}
