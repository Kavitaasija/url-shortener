package org.url.shortener.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class StrategyFactory {
  Map<URLStrategy, URLGenerationStrategy> strategyList;

  public StrategyFactory() {
    strategyList = new HashMap<>();
    strategyList.put(URLStrategy.RANDOM, new RandomGenerationStrategy());
  }
  public URLGenerationStrategy getStrategy(URLStrategy strategy) {
    return strategyList.get(strategy);
  }
  public URLGenerationStrategy getDefaultStrategy() {
    return strategyList.get(URLStrategy.RANDOM);
  }

}
