package org.url.shortener.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.url.shortener.dto.CreateShortUrlRequest;
import org.url.shortener.dto.CreateShortUrlResponse;
import org.url.shortener.dto.ErrorResponse;
import org.url.shortener.dto.GetLongUrlResponse;
import org.url.shortener.exception.InvalidUrlException;
import org.url.shortener.exception.NotFoundException;
import org.url.shortener.service.URLService;

@RestController
@RequestMapping("/api/urls")
public class URLController {
  
  private final URLService urlService;
  
  @Autowired
  public URLController(URLService urlService) {
    this.urlService = urlService;
  }
  

  @PostMapping
  public ResponseEntity<CreateShortUrlResponse> createShortUrl(
      @Valid @RequestBody CreateShortUrlRequest request) {
    String shortUrl = urlService.shortenUrl(request.getLongUrl());
    CreateShortUrlResponse response = new CreateShortUrlResponse(
        shortUrl, 
        request.getLongUrl(),
        "Short URL created successfully"
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  

  @GetMapping("/{shortUrl}")
  public ResponseEntity<GetLongUrlResponse> getLongUrl(@PathVariable String shortUrl) {
    String longUrl = urlService.getLongUrl(shortUrl);
    GetLongUrlResponse response = new GetLongUrlResponse(shortUrl, longUrl);
    return ResponseEntity.ok(response);
  }
  

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(InvalidUrlException.class)
  public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}

