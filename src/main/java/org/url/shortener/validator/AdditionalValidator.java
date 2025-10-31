package org.url.shortener.validator;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.url.shortener.exception.InvalidUrlException;

public class AdditionalValidator extends URLValidator {


  @Override
  protected  boolean doValidate(String url) {
    try {
      URL urlObj = new URL(url);
      urlObj.toURI();

      String protocol = urlObj.getProtocol().toLowerCase();
      if (!protocol.equals("http") && !protocol.equals("https")) {
        throw new InvalidUrlException("Only HTTP and HTTPS protocols are supported");
      }
    } catch (MalformedURLException e) {
      throw new InvalidUrlException("Malformed URL: " + url, e);
    } catch (URISyntaxException e) {
      throw new InvalidUrlException("Invalid URI syntax: " + url, e);
    }
    return true;
  }
}
