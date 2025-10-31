package org.url.shortener.validator;

public abstract class URLValidator {

  private URLValidator next;

  public URLValidator setNext(URLValidator next) {
    this.next = next;
    return next;
  }

  protected abstract boolean doValidate(String url);

  public void validate(String url) {
    if (doValidate(url) && next != null) {
      next.validate(url);
    }
  }
}
