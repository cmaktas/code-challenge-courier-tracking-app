package com.example.couriergeolocationtracker.service.consumer;

import com.example.couriergeolocationtracker.domain.entities.Courier;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import com.example.couriergeolocationtracker.service.consumer.strategy.DistanceUnitStrategy;
import com.example.couriergeolocationtracker.service.consumer.strategy.DistanceUnitStrategyFactory;
import com.example.couriergeolocationtracker.web.model.v1.response.CourierDistanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling operations related to couriers.
 */
@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final DataFlushService dataFlushService;
    private final DistanceUnitStrategyFactory strategyFactory;

    /**
     * Retrieves the total travel distance of a given courier and converts it to the requested unit.
     *
     * @param courierId ID of the courier
     * @param unit      The requested unit of measurement ("km" or "miles")
     * @return CourierDistanceResponse with converted distance and unit
     * @throws IllegalArgumentException if courier not found
     */
    public CourierDistanceResponse getTotalTravelDistance(Long courierId, String unit) {
        // Ensure database is up-to-date before fetching
        dataFlushService.flushCacheToDB();

        Courier courier = courierRepository.findById(courierId)
            .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        double distanceInMeters = courier.getTotalDistance();
        DistanceUnitStrategy strategy = strategyFactory.getStrategy(unit);

        double convertedDistance = strategy.convert(distanceInMeters);
        // Round to two decimals for readability
        convertedDistance = Math.round(convertedDistance * 100.0) / 100.0;

        return CourierDistanceResponse.builder()
            .courierId(courier.getId())
            .totalDistance(CourierDistanceResponse.Distance.builder()
                .value(convertedDistance)
                .unit(strategy.getUnitName())
                .build())
            .build();
    }

}