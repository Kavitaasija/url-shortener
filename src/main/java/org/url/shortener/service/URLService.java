package org.url.shortener.service;

import java.util.Optional;
import org.url.shortener.config.URLShortenerConfig;
import org.url.shortener.exception.DuplicateUrlIdentifierException;
import org.url.shortener.exception.MaxAttemptReachedException;
import org.url.shortener.exception.NotFoundException;
import org.url.shortener.model.LongUrl;
import org.url.shortener.observer.URLEventPublisher;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.strategy.URLGenerationStrategy;


public class URLService {

  private final URLRepository urlRepository;
  private final URLGenerationStrategy uniqueKeyGenerator;
  private final CalculateUrlExpiry calculateUrlExpiry;
  private final URLValidator urlValidator;
  private final URLShortenerConfig config;
  private final URLEventPublisher eventPublisher;


  public URLService(URLRepository urlRepository,
                    URLGenerationStrategy uniqueKeyGenerator,
                    URLEventPublisher eventPublisher) {
    this(urlRepository, uniqueKeyGenerator, new URLShortenerConfig(), eventPublisher);
  }

  public URLService(URLRepository urlRepository,
                    URLGenerationStrategy uniqueKeyGenerator,
                    URLShortenerConfig config,
                    URLEventPublisher eventPublisher) {
    if (urlRepository == null) {
      throw new IllegalArgumentException("URLRepository cannot be null");
    }
    if (uniqueKeyGenerator == null) {
      throw new IllegalArgumentException("UniqueKeyGenerator cannot be null");
    }
    if (config == null) {
      throw new IllegalArgumentException("URLShortenerConfig cannot be null");
    }
    if (eventPublisher == null) {
      throw new IllegalArgumentException("URLEventPublisher cannot be null");
    }
    this.urlRepository = urlRepository;
    this.uniqueKeyGenerator = uniqueKeyGenerator;
    this.config = config;
    this.eventPublisher = eventPublisher;
    this.calculateUrlExpiry = new CalculateUrlExpiry(config.getDefaultUrlExpirySeconds());
    this.urlValidator = new URLValidator();
  }

  public String shortenUrl(String longUrl) {
    if (config.isUrlValidationEnabled()) {
      urlValidator.validate(longUrl);
    }
    if (config.isDuplicateLongUrlsPrevented()) {
      Optional<String> existingShortUrl = urlRepository.findByLongUrl(longUrl);
      if (existingShortUrl.isPresent()) {
        System.out.println("Returning existing short URL for: " + longUrl);
        return existingShortUrl.get();
      }
    }
    return createShortUrl(longUrl, 0);
  }

  private String createShortUrl(String longUrl, int attemptCount) {
    if (attemptCount > config.getMaxCollisionRetryAttempts()) {
      throw new MaxAttemptReachedException(
          "Unable to generate unique identifier after " + attemptCount + " attempts. Please try again.");
    }

    String shortIdentifier = uniqueKeyGenerator.generateUniqueKey(longUrl, config.getShortUrlLength());

    try {
      Long expiryTime = calculateUrlExpiry.getDefaultExpiry();
      urlRepository.save(shortIdentifier, new LongUrl(longUrl, expiryTime));
      System.out.println("Created short URL: " + shortIdentifier + " for: " + longUrl);
      eventPublisher.publishUrlCreated(shortIdentifier, longUrl, expiryTime);
      return shortIdentifier;
    } catch (DuplicateUrlIdentifierException e) {
      System.out.println("Collision detected for: " + shortIdentifier + ", retrying...");
      eventPublisher.publishCollisionDetected(shortIdentifier, attemptCount + 1);
      return createShortUrl(longUrl, attemptCount + 1);
    }
  }

  public String getLongUrl(String shortUrlIdentifier) {
    if (shortUrlIdentifier == null || shortUrlIdentifier.trim().isEmpty()) {
      throw new IllegalArgumentException("Short URL identifier cannot be null or empty");
    }
    String longUrl = urlRepository.get(shortUrlIdentifier);
    if (longUrl == null) {
      throw new NotFoundException(shortUrlIdentifier);
    }
    eventPublisher.publishUrlAccessed(shortUrlIdentifier, longUrl);
    return longUrl;
  }

  public boolean deleteUrl(String shortUrlIdentifier) {
    if (urlRepository.exists(shortUrlIdentifier)) {
      urlRepository.remove(shortUrlIdentifier);
      System.out.println("Deleted short URL: " + shortUrlIdentifier);
      eventPublisher.publishUrlDeleted(shortUrlIdentifier);
      return true;
    }
    return false;
  }

}
