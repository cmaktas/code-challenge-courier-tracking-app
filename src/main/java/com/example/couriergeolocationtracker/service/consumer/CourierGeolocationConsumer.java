package com.example.couriergeolocationtracker.service.consumer;

import com.example.couriergeolocationtracker.domain.constants.ActiveMQConstants;
import com.example.couriergeolocationtracker.domain.dtos.CourierGeolocation;
import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierGeolocationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Consumer that listens to courier geolocation messages from the ActiveMQ queue.
 * Publishes an event for further processing (distance calculation, caching, etc.).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CourierGeolocationConsumer {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Consumes messages from the configured ActiveMQ queue, deserializes the JSON,
     * and publishes a {@link CourierGeolocationEvent}.
     *
     * @param message The raw JSON message from the queue.
     */
    @JmsListener(destination = ActiveMQConstants.QUEUE_NAME)
    public void consumeGeolocation(String message) {
        log.debug("Courier geolocation received from queue: {}", message);
        CourierGeolocation geolocation;
        try {
            geolocation = objectMapper.readValue(message, CourierGeolocation.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse CourierGeolocation from message. Reason={}", e.getMessage());
            throw new RuntimeException("Invalid CourierGeolocation JSON", e);
        }

        applicationEventPublisher.publishEvent(new CourierGeolocationEvent(geolocation));
        log.debug("CourierGeolocationEvent published for courierId={}", geolocation.getCourierId());
    }
}
