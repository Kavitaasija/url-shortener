package org.url.shortener.strategy;

import org.url.shortener.service.UniqueKeyGenerator;

public class RandomGenerationStrategy implements URLGenerationStrategy {
    private final UniqueKeyGenerator uniqueKeyGenerator;

    public RandomGenerationStrategy(UniqueKeyGenerator uniqueKeyGenerator) {
        this.uniqueKeyGenerator = uniqueKeyGenerator;
    }

    @Override
    public String generateUniqueKey(String longUrl, int length) {
        return uniqueKeyGenerator.generateUniqueKey(length);
    }
    
}
