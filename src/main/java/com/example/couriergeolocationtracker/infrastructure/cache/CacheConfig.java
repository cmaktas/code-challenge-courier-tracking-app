package com.example.couriergeolocationtracker.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration class for setting up Caffeine cache.
 */
@Configuration
public class CacheConfig {

    @Value("${courier-app.max-number-of-courier-entities}")
    private int maximumSize;

    @Value("${courier-app.cache.expire-duration-minutes}")
    private long expireDurationMinutes;

    /**
     * Bean definition for the cache manager using Caffeine.
     *
     * @return a {@link CacheManager} instance configured with cache settings.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("lastLocations", "accumulatedDistances", "lastStoreEntrances");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(Duration.ofMinutes(expireDurationMinutes)));
        return manager;
    }
}
