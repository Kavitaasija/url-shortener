package org.url.shortener.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.url.shortener.config.URLShortenerConfig;
import org.url.shortener.exception.DuplicateUrlIdentifierException;
import org.url.shortener.exception.MaxAttemptReachedException;
import org.url.shortener.exception.NotFoundException;
import org.url.shortener.model.LongUrl;
import org.url.shortener.observer.URLEventPublisher;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.strategy.StrategyFactory;
import org.url.shortener.validator.AdditionalValidator;
import org.url.shortener.validator.FormatValidator;
import org.url.shortener.validator.LengthValidator;
import org.url.shortener.validator.NullCheckValidator;
import org.url.shortener.validator.URLValidator;

@Service
public class BasicURLService implements URLService {

  private final URLRepository urlRepository;
  private final StrategyFactory strategyFactory;
  private final CalculateUrlExpiry calculateUrlExpiry;
  private final URLValidator urlValidator;
  private final URLShortenerConfig config;
  private final URLEventPublisher eventPublisher;


  @Autowired
  public BasicURLService(URLRepository urlRepository,
                         StrategyFactory strategyFactory,
                         URLEventPublisher eventPublisher) {
    this(urlRepository, strategyFactory, new URLShortenerConfig(), eventPublisher);
  }

  public BasicURLService(URLRepository urlRepository,
                         StrategyFactory strategyFactory,
                         URLShortenerConfig config,
                         URLEventPublisher eventPublisher) {
    if (urlRepository == null) {
      throw new IllegalArgumentException("URLRepository cannot be null");
    }

    if (config == null) {
      throw new IllegalArgumentException("URLShortenerConfig cannot be null");
    }
    if (eventPublisher == null) {
      throw new IllegalArgumentException("URLEventPublisher cannot be null");
    }
    this.urlRepository = urlRepository;
    this.strategyFactory = strategyFactory;
    this.config = config;
    this.eventPublisher = eventPublisher;
    this.calculateUrlExpiry = new CalculateUrlExpiry(config.getDefaultUrlExpirySeconds());
    this.urlValidator = new NullCheckValidator();
    this.urlValidator.setNext(new LengthValidator())
        .setNext(new FormatValidator())
        .setNext(new AdditionalValidator());
  }


  @Override
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

    String shortIdentifier = strategyFactory.getDefaultStrategy().generateUniqueKey(longUrl, config.getShortUrlLength());

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

  @Override
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
