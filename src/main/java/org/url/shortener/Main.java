package org.url.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.url.shortener.config.URLShortenerConfig;
import org.url.shortener.observer.AnalyticsListener;
import org.url.shortener.observer.LoggingListener;
import org.url.shortener.observer.MetricsListener;
import org.url.shortener.observer.URLEventPublisher;
import org.url.shortener.repository.DefaultRepository;
import org.url.shortener.repository.URLRepository;
import org.url.shortener.scheduler.DeleteExpiryUrlsScheduler;
import org.url.shortener.scheduler.SchedulerConfig;
import org.url.shortener.strategy.UniqueKeyGenerator;
import org.url.shortener.service.BasicURLService;
import org.url.shortener.service.URLService;
import org.url.shortener.strategy.RandomGenerationStrategy;

@SpringBootApplication
public class Main {
  
  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
  
  @Bean
  public URLShortenerConfig urlShortenerConfig() {
    return new URLShortenerConfig();
  }
  
  @Bean
  public URLRepository urlRepository() {
    return new DefaultRepository();
  }
  
  @Bean
  public URLEventPublisher urlEventPublisher() {
    return new URLEventPublisher();
  }
  
  @Bean
  public LoggingListener loggingListener(URLEventPublisher eventPublisher) {
    LoggingListener listener = new LoggingListener();
    eventPublisher.subscribe(listener);
    return listener;
  }
  
  @Bean
  public MetricsListener metricsListener(URLEventPublisher eventPublisher) {
    MetricsListener listener = new MetricsListener();
    eventPublisher.subscribe(listener);
    return listener;
  }
  
  @Bean
  public AnalyticsListener analyticsListener(URLEventPublisher eventPublisher) {
    AnalyticsListener listener = new AnalyticsListener();
    eventPublisher.subscribe(listener);
    return listener;
  }

  
  @Bean
  public DeleteExpiryUrlsScheduler deleteExpiryUrlsScheduler(URLRepository urlRepository,
                                                              URLShortenerConfig config,
                                                              URLEventPublisher eventPublisher) {
    SchedulerConfig schedulerConfig = new SchedulerConfig(
        config.getCleanupFrequencySeconds(),
        config.getSchedulerThreadPoolSize()
    );
    DeleteExpiryUrlsScheduler scheduler = new DeleteExpiryUrlsScheduler(
        urlRepository,
        schedulerConfig,
        eventPublisher
    );
    scheduler.start();
    return scheduler;
  }
  
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    System.out.println("=== URL Shortener Service Started ===");
    System.out.println("🎯 Observer Pattern: Event listeners with ASYNC notification!");
    System.out.println("🌐 REST API is now available!");
    System.out.println("   POST /api/urls - Create short URL");
    System.out.println("   GET  /api/urls/{shortUrl} - Get long URL");
  }
}