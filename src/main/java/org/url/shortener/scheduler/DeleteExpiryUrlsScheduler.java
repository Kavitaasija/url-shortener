package org.url.shortener.scheduler;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.url.shortener.repository.URLRepository;

public class DeleteExpiryUrlsScheduler {

  private final URLRepository urlRepository;
  private final SchedulerConfig schedulerConfig;
  private ScheduledExecutorService scheduledExecutorService;
  private final AtomicBoolean isRunning;

  public DeleteExpiryUrlsScheduler(URLRepository urlRepository, SchedulerConfig schedulerConfig) {
    if (urlRepository == null) {
      throw new IllegalArgumentException("URLRepository cannot be null");
    }
    if (schedulerConfig == null) {
      throw new IllegalArgumentException("SchedulerConfig cannot be null");
    }
    this.urlRepository = urlRepository;
    this.schedulerConfig = schedulerConfig;
    this.isRunning = new AtomicBoolean(false);
  }

  public void start() {
    if (isRunning.compareAndSet(false, true)) {
      scheduledExecutorService = new ScheduledThreadPoolExecutor(
          schedulerConfig.getThreadPoolSize(),
          r -> {
            Thread thread = new Thread(r, "URL-Cleanup-Thread");
            thread.setDaemon(true); // Daemon thread won't prevent JVM shutdown
            return thread;
          }
      );
      
      scheduledExecutorService.scheduleAtFixedRate(
          this::deleteExpiredUrls,
          0,
          schedulerConfig.getFrequency(),
          TimeUnit.SECONDS
      );
      
      System.out.println("URL cleanup scheduler started with frequency: " 
          + schedulerConfig.getFrequency() + " seconds");
    }
  }

  public void shutdown() {
    if (isRunning.compareAndSet(true, false)) {
      if (scheduledExecutorService != null) {
        System.out.println("Shutting down URL cleanup scheduler...");
        scheduledExecutorService.shutdown();
        try {
          if (!scheduledExecutorService.awaitTermination(30, TimeUnit.SECONDS)) {
            scheduledExecutorService.shutdownNow();
          }
          System.out.println("URL cleanup scheduler shut down successfully");
        } catch (InterruptedException e) {
          scheduledExecutorService.shutdownNow();
          Thread.currentThread().interrupt();
        }
      }
    }
  }


  private void deleteExpiredUrls() {
    try {
      long currentTime = Instant.now().getEpochSecond();
      List<String> expiredUrls = urlRepository.getAllExpired(currentTime);
      
      if (!expiredUrls.isEmpty()) {
        expiredUrls.forEach(urlRepository::remove);
      }
    } catch (Exception e) {
      System.err.println("Error during URL cleanup: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
