package org.url.shortener.strategy;

import org.springframework.stereotype.Service;

@Service
public class RandomGenerationStrategy implements URLGenerationStrategy {
    private final UniqueKeyGenerator uniqueKeyGenerator;

    public RandomGenerationStrategy() {
        this.uniqueKeyGenerator = new UniqueKeyGenerator();
    }

    @Override
    public String generateUniqueKey(String longUrl, int length) {
        return uniqueKeyGenerator.generateUniqueKey(length);
    }
    
}
