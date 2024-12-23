package com.example.couriergeolocationtracker.service.consumer.observer.events;

import com.example.couriergeolocationtracker.domain.dtos.CourierGeolocation;

/**
 * Event indicating a raw geolocation message was received from the queue.
 */
public record CourierGeolocationEvent(CourierGeolocation geolocation) implements CourierEvent {
}
