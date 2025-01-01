package com.example.couriergeolocationtracker.service.consumer.observer.observers;

import com.example.couriergeolocationtracker.domain.entities.Courier;
import com.example.couriergeolocationtracker.domain.entities.Store;
import com.example.couriergeolocationtracker.domain.entities.StoreEntranceLog;
import com.example.couriergeolocationtracker.infrastructure.cache.StoreCache;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import com.example.couriergeolocationtracker.infrastructure.repository.StoreEntranceLogRepository;
import com.example.couriergeolocationtracker.service.consumer.CourierCacheService;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierEvent;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierReadyEvent;
import com.example.couriergeolocationtracker.utils.HaversineDistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.List;

/**
 * Observer that handles store-entrance logic after the Courier is confirmed
 * in the database. Also uses the lat/lng from {@link CourierReadyEvent}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StoreEntranceObserver {

    private final CourierRepository courierRepository;
    private final StoreEntranceLogRepository storeEntranceLogRepository;
    private final CourierCacheService cacheService;
    private final StoreCache storeCache;

    @EventListener
    @Transactional
    public void onCourierEvent(CourierEvent event) {
        if (event instanceof CourierReadyEvent(Long courierId, double lat, double lng)) {
            log.debug("StoreEntranceObserver triggered for courierId={}, lat={}, lng={}", courierId, lat, lng);
            Courier courier = courierRepository.findById(courierId).orElse(null);
            if (courier == null) {
                log.error("Courier with ID={} not found. Skipping store entrance logic.", courierId);
                return;
            }
            checkAndLogStoreEntrance(courier, lat, lng);
        }
    }

    /**
     * Checks if the courier has entered the radius of any store and logs the entrance if valid.
     */
    private void checkAndLogStoreEntrance(Courier courier, double lat, double lng) {
        List<Store> stores = storeCache.getAllStores();
        for (Store store : stores) {
            if (isCourierWithinStoreRadius(store, lat, lng)) {
                attemptStoreEntranceLog(courier, store);
            }
        }
    }

    /**
     * Determines if the courier is within 100m of a store based on current lat/lng.
     */
    private boolean isCourierWithinStoreRadius(Store store, double lat, double lng) {
        double dist = HaversineDistanceCalculator.calculateDistanceInMeters(
            store.getLat(), store.getLng(),
            lat, lng
        );
        return dist <= 100.0;
    }

    /**
     * Attempts to log a store entrance if the courier hasn't entered in the last minute.
     */
    private void attemptStoreEntranceLog(Courier courier, Store store) {
        Long lastEntrance = cacheService.getLastStoreEntrance(courier.getId(), store.getStoreName());
        if (ObjectUtils.isEmpty(lastEntrance) || (System.currentTimeMillis() - lastEntrance) > 60000) {
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
        log.info("CourierId={} entered store='{}'", courier.getId(), store.getStoreName());
    }
}
