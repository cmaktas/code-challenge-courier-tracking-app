package com.example.couriergeolocationtracker.service.consumer.observer.observers;

import com.example.couriergeolocationtracker.domain.dtos.CourierGeolocation;
import com.example.couriergeolocationtracker.domain.entities.Courier;
import com.example.couriergeolocationtracker.infrastructure.repository.CourierRepository;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierEvent;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierGeolocationEvent;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Observer responsible for creating or finding the courier in the database.
 * After successful DB operations, it publishes a {@link CourierReadyEvent}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourierDatabaseObserver {

    private final CourierRepository courierRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    @EventListener
    public void onCourierEvent(CourierEvent event) {
        if (event instanceof CourierGeolocationEvent(CourierGeolocation geo)) {
            log.debug("CourierDatabaseObserver triggered for courierId={}", geo.getCourierId());
            Courier courier = findOrCreateCourier(geo.getCourierId());
            applicationEventPublisher.publishEvent(
                    new CourierReadyEvent(courier.getId(), geo.getLat(), geo.getLng())
            );
            log.debug("CourierReadyEvent published for courierId={}, lat={}, lng={}",
                courier.getId(), geo.getLat(), geo.getLng());
        }
    }

    /**
     * Finds an existing courier by ID or creates a new one if none exists.
     */
    private Courier findOrCreateCourier(Long courierId) {
        return courierRepository.findById(courierId)
            .orElseGet(() -> {
                log.debug("No courier found for ID={}. Creating new courier.", courierId);
                return courierRepository.save(
                    Courier.builder()
                        .storeEntranceCount(0)
                        .totalDistance(0)
                        .build()
                );
            });
    }
}
