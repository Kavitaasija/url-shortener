package org.url.shortener.scheduler;


import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.url.shortener.repository.URLRepository;

public class DeleteExpiryUrlsScheduler {

  private final URLRepository urlRepository;
  private final SchedulerConfig schedulerConfig;


  public DeleteExpiryUrlsScheduler(URLRepository urlRepository, SchedulerConfig schedulerConfig) {
    this.urlRepository = urlRepository;
    this.schedulerConfig = schedulerConfig;
  }

  public void scheduleExpiredUrlsDelete() {
    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(5);
    ScheduledFuture<?> scheduledFuture =
        scheduledExecutorService.scheduleAtFixedRate(this::deleteExpiredOne, 0, schedulerConfig.frequency,
            TimeUnit.SECONDS);
    System.out.println(scheduledFuture.state());
  }

  private void deleteExpiredOne() {
    List<String> urls = urlRepository.getAll(Instant.now().getEpochSecond());
    urls.forEach(urlRepository::remove);
  }
}
