package com.example.couriergeolocationtracker.service.consumer.observer.observers;

import com.example.couriergeolocationtracker.domain.entities.Courier;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import com.example.couriergeolocationtracker.service.consumer.CourierCacheService;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierEvent;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierReadyEvent;
import com.example.couriergeolocationtracker.utils.HaversineDistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

/**
 * Observer that handles distance calculation and caching,
 * after the Courier is confirmed in the database.
 */
@Slf4j
@RequiredArgsConstructor
public class DistanceObserver implements CourierEventObserver {

    private final CourierRepository courierRepository;
    private final CourierCacheService cacheService;

    @Override
    public void onCourierEvent(CourierEvent event) {
        if (event instanceof CourierReadyEvent(Long courierId, double lat, double lng)) {
            log.debug("DistanceObserver triggered for courierId={}, lat={}, lng={}", courierId, lat, lng);
            Courier courier = courierRepository.findById(courierId).orElse(null);
            if (ObjectUtils.isEmpty(courier)) {
                log.error("Courier not found for ID={}. Cannot process distance updates.", courierId);
                return;
            }
            processDistanceUpdates(courier, lat, lng);
        }
    }

    /**
     * Calculates incremental distance and updates the cache with the new location.
     */
    private void processDistanceUpdates(Courier courier, double lat, double lng) {
        double[] lastLocation = cacheService.getLastKnownLocation(courier.getId());
        if (!ObjectUtils.isEmpty(lastLocation)) {
            double dist = HaversineDistanceCalculator.calculateDistanceInMeters(
                lastLocation[0], lastLocation[1],
                lat, lng
            );
            cacheService.addDistance(courier.getId(), dist);
            log.debug("DistanceObserver added distance={} for courierId={}", dist, courier.getId());
        }
        cacheService.updateLastKnownLocation(courier.getId(), lat, lng);
    }
}
