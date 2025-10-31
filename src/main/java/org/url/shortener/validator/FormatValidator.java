package org.url.shortener.validator;

import java.util.regex.Pattern;
import org.url.shortener.exception.InvalidUrlException;

public class FormatValidator extends URLValidator {

  private static final Pattern URL_PATTERN = Pattern.compile(
      "^(https?://)([\\w\\-]+(\\.[\\w\\-]+)+)(:[0-9]+)?(/.*)?$",
      Pattern.CASE_INSENSITIVE
  );


  @Override
  protected boolean doValidate(String url) {
    if (!URL_PATTERN.matcher(url).matches()) {
      throw new InvalidUrlException("Invalid URL format: " + url);
    }
    return true;
  }
}
