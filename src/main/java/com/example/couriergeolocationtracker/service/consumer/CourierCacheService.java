package com.example.couriergeolocationtracker.service.consumer;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


@Service
@RequiredArgsConstructor
public class CourierCacheService {

    private final CacheManager cacheManager;

    // Caches:
    // "lastLocations" -> key: courierId, value: double[]{lat, lng}
    // "accumulatedDistances" -> key: courierId, value: Double (distance in meters)
    // "lastStoreEntrances" -> key: courierId_storeName, value: Long (timestamp in millis of last entrance)

    public void updateLastKnownLocation(Long courierId, double lat, double lng) {
        Cache cache = cacheManager.getCache("lastLocations");
        if (!ObjectUtils.isEmpty(cache)) {
            cache.put(courierId, new double[]{lat, lng});
        }
    }

    public double[] getLastKnownLocation(Long courierId) {
        Cache cache = cacheManager.getCache("lastLocations");
        return (ObjectUtils.isEmpty(cache)) ? null : cache.get(courierId, double[].class);
    }

    public void addDistance(Long courierId, double distanceToAdd) {
        Cache cache = cacheManager.getCache("accumulatedDistances");
        if (!ObjectUtils.isEmpty(cache)) {
            Double current = cache.get(courierId, Double.class);
            if (ObjectUtils.isEmpty(current)) current = 0.0;
            cache.put(courierId, current + distanceToAdd);
        }
    }

    public Double getAccumulatedDistance(Long courierId) {
        Cache cache = cacheManager.getCache("accumulatedDistances");
        return (ObjectUtils.isEmpty(cache)) ? 0.0 : cache.get(courierId, Double.class);
    }

    public void recordStoreEntrance(Long courierId, String storeName) {
        Cache cache = cacheManager.getCache("lastStoreEntrances");
        if (!ObjectUtils.isEmpty(cache)) {
            cache.put(courierId + "_" + storeName, System.currentTimeMillis());
        }
    }

    public Long getLastStoreEntrance(Long courierId, String storeName) {
        Cache cache = cacheManager.getCache("lastStoreEntrances");
        return (ObjectUtils.isEmpty(cache)) ? null : cache.get(courierId + "_" + storeName, Long.class);
    }
}

