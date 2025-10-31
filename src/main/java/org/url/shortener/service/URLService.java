package org.url.shortener.service;

import java.util.Optional;
import org.url.shortener.config.URLShortenerConfig;
import org.url.shortener.exception.DuplicateUrlIdentifierException;
import org.url.shortener.exception.MaxAttemptReachedException;
import org.url.shortener.exception.NotFoundException;
import org.url.shortener.model.LongUrl;
import org.url.shortener.observer.URLEventPublisher;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.validator.AdditionalValidator;
import org.url.shortener.validator.FormatValidator;
import org.url.shortener.validator.LengthValidator;
import org.url.shortener.validator.NullCheckValidator;
import org.url.shortener.validator.URLValidator;
import org.url.shortener.strategy.URLGenerationStrategy;

public interface URLService {
  String shortenUrl(String longUrl);
  String getLongUrl(String shortUrlIdentifier);
}

