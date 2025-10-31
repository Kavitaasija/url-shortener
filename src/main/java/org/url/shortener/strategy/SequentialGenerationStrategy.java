package org.url.shortener.strategy;

import java.util.concurrent.atomic.AtomicLong;

public class SequentialGenerationStrategy implements URLGenerationStrategy {

    private final AtomicLong counter;

    public SequentialGenerationStrategy() {
        this.counter = new AtomicLong(10000);
    }

    @Override
    public String generateUniqueKey(String longUrl, int length) {
        long id  = counter.incrementAndGet();
        return String.valueOf(id);
    }
    
}
