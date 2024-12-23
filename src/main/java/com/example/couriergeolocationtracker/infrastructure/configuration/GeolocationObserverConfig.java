package com.example.couriergeolocationtracker.infrastructure.configuration;

import com.example.couriergeolocationtracker.infrastructure.cache.StoreCache;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import com.example.couriergeolocationtracker.infrastructure.repository.StoreEntranceLogRepository;
import com.example.couriergeolocationtracker.service.consumer.CourierCacheService;
import com.example.couriergeolocationtracker.service.consumer.observer.observers.CourierDatabaseObserver;
import com.example.couriergeolocationtracker.service.consumer.observer.observers.DistanceObserver;
import com.example.couriergeolocationtracker.service.consumer.observer.observers.StoreEntranceObserver;
import com.example.couriergeolocationtracker.service.consumer.observer.publisher.CourierEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the three-observer:
 * 1) CourierDatabaseObserver
 * 2) DistanceObserver
 * 3) StoreEntranceObserver
 */
@Slf4j
@Configuration
public class GeolocationObserverConfig {

    @Bean
    public CourierEventPublisher courierEventPublisher(
        CourierRepository courierRepository,
        CourierCacheService cacheService,
        StoreEntranceLogRepository storeEntranceLogRepository,
        StoreCache storeCache
    ) {
        log.debug("Initializing CourierEventPublisher bean...");

        CourierEventPublisher publisher = new CourierEventPublisher();

        CourierDatabaseObserver dbObserver = new CourierDatabaseObserver(courierRepository, publisher);
        publisher.addObserver(dbObserver);

        DistanceObserver distanceObserver = new DistanceObserver(courierRepository, cacheService);
        publisher.addObserver(distanceObserver);

        StoreEntranceObserver storeObserver = new StoreEntranceObserver(
            courierRepository,
            storeEntranceLogRepository,
            cacheService,
            storeCache
        );
        publisher.addObserver(storeObserver);

        log.info("CourierEventPublisher bean created with {}, {}, and {}",
            dbObserver.getClass().getSimpleName(),
            distanceObserver.getClass().getSimpleName(),
            storeObserver.getClass().getSimpleName());

        return publisher;
    }
}
