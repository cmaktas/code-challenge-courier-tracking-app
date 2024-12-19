package com.example.couriergeolocationtracker.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("lastLocations", "accumulatedDistances", "lastStoreEntrances");
        manager.setCaffeine(Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(java.time.Duration.ofHours(1)));
        return manager;
    }
}
