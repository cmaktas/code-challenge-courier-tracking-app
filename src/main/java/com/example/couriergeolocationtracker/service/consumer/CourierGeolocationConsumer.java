package com.example.couriergeolocationtracker.service.consumer;

import com.example.couriergeolocationtracker.domain.constants.ActiveMQConstants;
import com.example.couriergeolocationtracker.domain.dtos.CourierGeolocation;
import com.example.couriergeolocationtracker.domain.entities.Courier;
import com.example.couriergeolocationtracker.domain.entities.Store;
import com.example.couriergeolocationtracker.domain.entities.StoreEntranceLog;
import com.example.couriergeolocationtracker.infrastructure.cache.StoreCache;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import com.example.couriergeolocationtracker.infrastructure.repository.StoreEntranceLogRepository;
import com.example.couriergeolocationtracker.utils.HaversineDistanceCalculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.List;

/**
 * Consumer that listens to courier geolocation messages from the ActiveMQ queue.
 * Updates cache with new locations, calculates incremental distances, and logs store entrances.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourierGeolocationConsumer {

    private final CourierCacheService cacheService;
    private final CourierRepository courierRepository;
    private final StoreEntranceLogRepository storeEntranceLogRepository;
    private final StoreCache storeCache;
    private final ObjectMapper objectMapper;

    /**
     * Consumes messages from the configured ActiveMQ queue.
     * Converts the JSON string into a CourierGeolocation object.
     *
     * @param message JSON string representing a CourierGeolocation
     * @throws JsonProcessingException if message cannot be parsed
     */
    @Transactional
    @JmsListener(destination = ActiveMQConstants.QUEUE_NAME)
    public void consumeGeolocation(String message) throws JsonProcessingException {
        log.debug("Courier geolocation received from queue: {}", message);
        CourierGeolocation geolocation = objectMapper.readValue(message, CourierGeolocation.class);

        Courier courier = findOrCreateCourier(geolocation.getCourierId());
        processDistanceUpdates(courier, geolocation);
        checkAndLogStoreEntrance(courier, geolocation);
    }

    /**
     * Finds an existing courier by ID or creates a new one if none exists.
     */
    private Courier findOrCreateCourier(Long courierId) {
        return courierRepository.findById(courierId)
                .orElseGet(() -> {
                    Courier c = Courier.builder().totalDistance(0).storeEntranceCount(0).build();
                    return courierRepository.save(c);
                });
    }

    /**
     * Calculates incremental distance and updates the cache with the new location.
     */
    private void processDistanceUpdates(Courier courier, CourierGeolocation geolocation) {
        double[] lastLocation = cacheService.getLastKnownLocation(courier.getId());
        if (!ObjectUtils.isEmpty(lastLocation)) {
            double dist = HaversineDistanceCalculator.calculateDistanceInMeters(
                    lastLocation[0], lastLocation[1],
                    geolocation.getLat(), geolocation.getLng()
            );
            cacheService.addDistance(courier.getId(), dist);
        }
        cacheService.updateLastKnownLocation(courier.getId(), geolocation.getLat(), geolocation.getLng());
    }

    /**
     * Checks if the courier has entered the radius of any store and logs the entrance if valid.
     */
    private void checkAndLogStoreEntrance(Courier courier, CourierGeolocation geolocation) {
        List<Store> stores = storeCache.getAllStores();
        for (Store store : stores) {
            if (isCourierWithinStoreRadius(store, geolocation)) {
                attemptStoreEntranceLog(courier, store);
            }
        }
    }

    /**
     * Determines if the courier is within 100m of a store.
     */
    private boolean isCourierWithinStoreRadius(Store store, CourierGeolocation geolocation) {
        double dist = HaversineDistanceCalculator.calculateDistanceInMeters(
                store.getLat(), store.getLng(),
                geolocation.getLat(), geolocation.getLng()
        );
        return dist <= 100.0;
    }

    /**
     * Attempts to log a store entrance if the courier hasn't entered the store in the last minute.
     */
    private void attemptStoreEntranceLog(Courier courier, Store store) {
        Long lastEntrance = cacheService.getLastStoreEntrance(courier.getId(), store.getStoreName());
        long now = System.currentTimeMillis();
        if (ObjectUtils.isEmpty(lastEntrance) || (now - lastEntrance) > 60000) {
            logStoreEntrance(courier, store);
        }
    }

    /**
     * Logs the store entrance event, updates the courier's entrance count, and records it in cache.
     */
    private void logStoreEntrance(Courier courier, Store store) {
        courier.setStoreEntranceCount(courier.getStoreEntranceCount() + 1);
        courierRepository.save(courier);

        storeEntranceLogRepository.save(StoreEntranceLog.builder()
                .courierId(courier.getId())
                .storeName(store.getStoreName())
                .entryTime(Instant.now())
                .build());

        cacheService.recordStoreEntrance(courier.getId(), store.getStoreName());
    }
}
