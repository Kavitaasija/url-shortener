package org.url.shortener.service;

import org.url.shortener.exception.DuplicateUrlIdentifierException;
import org.url.shortener.exception.MaxAttemptReachedException;
import org.url.shortener.exception.NotFoundException;
import org.url.shortener.model.LongUrl;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.scheduler.DeleteExpiryUrlsScheduler;
import org.url.shortener.scheduler.SchedulerConfig;

public class URLService {

  public static final int FREQUENCY = 5;
  public static final double MAX_ATTEMPT = 5;
  URLRepository urlRepository;
  UniqueKeyGenerator uniqueKeyGenerator;
  CalculateUrlExpiry calculateUrlExpiry;
  DeleteExpiryUrlsScheduler deleteExpiryUrlsScheduler;

  private final static int URL_LENGTH = 6;

  public URLService(URLRepository urlRepository,
                    UniqueKeyGenerator uniqueKeyGenerator) {
    this.urlRepository = urlRepository;
    this.uniqueKeyGenerator = uniqueKeyGenerator;
    this.calculateUrlExpiry = new CalculateUrlExpiry();
    this.deleteExpiryUrlsScheduler = new DeleteExpiryUrlsScheduler(this.urlRepository, new SchedulerConfig(FREQUENCY));
    deleteExpiryUrlsScheduler.scheduleExpiredUrlsDelete();
  }

  public String save(String url) {
    return save(url, 0);
  }

  private String save(String url, int attemptCount) {
    if (attemptCount <= MAX_ATTEMPT) {
      String generateUniqueIdentifier = uniqueKeyGenerator.generateUniqueKey(URL_LENGTH);
      try {
        urlRepository.save(generateUniqueIdentifier, new LongUrl(url, calculateUrlExpiry.getDefaultExpiry()));
        return generateUniqueIdentifier;
      } catch (DuplicateUrlIdentifierException e) {
        return save(url, attemptCount + 1);
      }
    }
    throw new MaxAttemptReachedException("Unable to generate unique identifier try again in some time");
  }

  public String get(String urlIdentifier) {
    String url = urlRepository.get(urlIdentifier);
    if (url == null) {
      throw new NotFoundException(urlIdentifier);
    }
    return url;
  }
  // URL validator
  // Add check for duplicate long url
}
