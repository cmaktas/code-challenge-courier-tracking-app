package com.example.couriergeolocationtracker.service.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * Service for managing courier-related cache data.
 */
@Service
@RequiredArgsConstructor
public class CourierCacheService {

    private final CacheManager cacheManager;

    /**
     * Updates the last known location of a courier in the cache.
     *
     * @param courierId ID of the courier.
     * @param lat       Latitude of the courier's location.
     * @param lng       Longitude of the courier's location.
     */
    public void updateLastKnownLocation(Long courierId, double lat, double lng) {
        Cache cache = cacheManager.getCache("lastLocations");
        if (!ObjectUtils.isEmpty(cache)) {
            cache.put(courierId, new double[] {lat, lng});
        }
    }

    /**
     * Retrieves the last known location of a courier from the cache.
     *
     * @param courierId ID of the courier.
     * @return A double array containing latitude and longitude, or {@code null} if not available.
     */
    public double[] getLastKnownLocation(Long courierId) {
        Cache cache = cacheManager.getCache("lastLocations");
        return (ObjectUtils.isEmpty(cache)) ? null : cache.get(courierId, double[].class);
    }

    /**
     * Adds a distance to the accumulated distance for a courier.
     *
     * @param courierId     ID of the courier.
     * @param distanceToAdd Distance to add in meters.
     */
    public void addDistance(Long courierId, double distanceToAdd) {
        Cache cache = cacheManager.getCache("accumulatedDistances");
        if (!ObjectUtils.isEmpty(cache)) {
            Double current = cache.get(courierId, Double.class);
            if (ObjectUtils.isEmpty(current)) {
                current = 0.0;
            }
            cache.put(courierId, current + distanceToAdd);
        }
    }

    /**
     * Retrieves the accumulated distance for a courier from the cache.
     *
     * @param courierId ID of the courier.
     * @return The accumulated distance in meters, or 0.0 if not available.
     */
    public Double getAccumulatedDistance(Long courierId) {
        Cache cache = cacheManager.getCache("accumulatedDistances");
        return (ObjectUtils.isEmpty(cache)) ? 0.0 : cache.get(courierId, Double.class);
    }

    /**
     * Records the timestamp of the courier's entrance into a store.
     *
     * @param courierId ID of the courier.
     * @param storeName Name of the store.
     */
    public void recordStoreEntrance(Long courierId, String storeName) {
        Cache cache = cacheManager.getCache("lastStoreEntrances");
        if (!ObjectUtils.isEmpty(cache)) {
            cache.put(courierId + "_" + storeName, System.currentTimeMillis());
        }
    }

    /**
     * Retrieves the timestamp of the last entrance of a courier into a store.
     *
     * @param courierId ID of the courier.
     * @param storeName Name of the store.
     * @return The timestamp in milliseconds, or {@code null} if not available.
     */
    public Long getLastStoreEntrance(Long courierId, String storeName) {
        Cache cache = cacheManager.getCache("lastStoreEntrances");
        return (ObjectUtils.isEmpty(cache)) ? null : cache.get(courierId + "_" + storeName, Long.class);
    }
}
