package com.example.couriergeolocationtracker.service.consumer;

import com.example.couriergeolocationtracker.domain.entities.Courier;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

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
     * Periodically flushes the cached distances to the database.
     * Logs how many couriers were updated for reporting purposes.
     */
    @Scheduled(fixedRateString = "${courier-app.data-flush.rate-ms}")
    public void flushCacheToDB() {
        log.info("Starting flushCacheToDB process...");
        List<Courier> couriers = courierRepository.findAll();
        int updatedCount = 0;

        for (Courier courier : couriers) {
            Double cachedDistance = cacheService.getAccumulatedDistance(courier.getId());
            if (!ObjectUtils.isEmpty(cachedDistance) && cachedDistance > courier.getTotalDistance()) {
                log.debug("Updating courier [{}] totalDistance from {} to {}", courier.getId(), courier.getTotalDistance(), cachedDistance);
                courier.setTotalDistance(cachedDistance);
                courierRepository.save(courier);
                updatedCount++;
            } else {
                log.trace("No update required for courier [{}]. Current totalDistance: {}, cachedDistance: {}",
                        courier.getId(), courier.getTotalDistance(), cachedDistance);
            }
        }

        log.info("Completed flushCacheToDB process. {} courier(s) updated.", updatedCount);
    }
}
