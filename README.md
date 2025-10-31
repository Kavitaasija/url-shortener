Builder Pattern - for URLShortenerConfig to make configuration more flexible
Factory Pattern - for creating key generators with different strategies (random, hash-based, counter-based)
Strategy Pattern - for URL shortening algorithms (random, hash-based, sequential)
Dependency Injection / Factory Pattern - for better dependency management (currently manual in Main)
Chain of Responsibility - for validation pipeline
Observer Pattern - for monitoring URL creation/deletion events
Decorator Pattern - for adding caching, logging, or metrics
Template Method Pattern - for different repository implementations
Singleton Pattern - for repository or configuration (though dependency injection is better)