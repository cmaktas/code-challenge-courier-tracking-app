package com.example.couriergeolocationtracker.service.consumer.observer.events;

/**
 * Event indicating that a courier has been initialized in the database
 * and is now ready for distance and store-entrance logic.
 */
public record CourierReadyEvent(Long courierId, double lat, double lng) implements CourierEvent {
}
