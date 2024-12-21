package com.example.couriergeolocationtracker.service.consumer;

import com.example.couriergeolocationtracker.domain.entities.Courier;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * Service responsible for periodically flushing cached courier distances into the database.
 * Ensures the totalDistance field is kept in sync with the cached distances.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataFlushService {

    private final CourierRepository courierRepository;
    private final CourierCacheService cacheService;

    /**
     * Periodically flushes the cached distances to the database within a single transaction.
     * Logs how many couriers were updated for reporting purposes.
     */
    @Scheduled(fixedRateString = "${courier-app.data-flush.rate-ms}")
    @Transactional
    public void flushCacheToDB() {
        log.debug("Starting flushCacheToDB process...");

        int updatedCount = courierRepository.findAll().stream()
                .mapToInt(courier -> updateDistanceIfStale(courier) ? 1 : 0)
                .sum();

        log.info("Completed flushCacheToDB process. {} courier(s) updated.", updatedCount);
    }

    /**
     * Checks if the courier's cached distance exceeds its stored distance
     * and updates the database if necessary.
     *
     * @param courier The courier entity to check and possibly update.
     * @return true if the courier was updated; false otherwise.
     */
    private boolean updateDistanceIfStale(Courier courier) {
        Double cachedDistance = cacheService.getAccumulatedDistance(courier.getId());
        if (!ObjectUtils.isEmpty(cachedDistance) && cachedDistance > courier.getTotalDistance()) {
            log.debug("Updating courier [{}] totalDistance from {} to {}",
                    courier.getId(), courier.getTotalDistance(), cachedDistance);
            courier.setTotalDistance(cachedDistance);
            courierRepository.save(courier);

            return true;
        }
        log.trace("No update required for courier [{}]. Current totalDistance: {}, cachedDistance: {}",
                courier.getId(), courier.getTotalDistance(), cachedDistance);
        return false;
    }
}
