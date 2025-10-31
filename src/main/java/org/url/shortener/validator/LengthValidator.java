package org.url.shortener.validator;

import org.url.shortener.exception.InvalidUrlException;

public class LengthValidator extends URLValidator {
  public static final int MAX_URL_LENGTH = 2048;

  @Override
  protected boolean doValidate(String url) {
    if (url.length() > MAX_URL_LENGTH) {
      throw new InvalidUrlException("URL cannot be null or empty");
    }
    return true;
  }
}
