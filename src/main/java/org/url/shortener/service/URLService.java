package org.url.shortener.service;

public interface URLService {
  String shortenUrl(String longUrl);
  String getLongUrl(String shortUrlIdentifier);
}

