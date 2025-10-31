package org.url.shortener.scheduler;


public class SchedulerConfig {
  private final Integer frequency;
  private final Integer threadPoolSize;

  public SchedulerConfig(Integer frequency, Integer threadPoolSize) {
    if (frequency == null || frequency <= 0) {
      throw new IllegalArgumentException("Frequency must be positive");
    }
    if (threadPoolSize == null || threadPoolSize <= 0) {
      throw new IllegalArgumentException("Thread pool size must be positive");
    }
    this.frequency = frequency;
    this.threadPoolSize = threadPoolSize;
  }
  
  public Integer getFrequency() {
    return frequency;
  }
  
  public Integer getThreadPoolSize() {
    return threadPoolSize;
  }
}
