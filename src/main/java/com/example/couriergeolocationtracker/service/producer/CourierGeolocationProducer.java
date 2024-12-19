package com.example.couriergeolocationtracker.service.producer;

import com.example.couriergeolocationtracker.domain.constants.ActiveMQConstants;
import com.example.couriergeolocationtracker.domain.dtos.CourierGeolocation;
import com.example.couriergeolocationtracker.domain.entities.Store;
import com.example.couriergeolocationtracker.infrastructure.cache.StoreCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

/**
 * Service responsible for producing courier geolocation messages and sending them to the queue.
 * Uses the configured rate and range of courier IDs from application.yml.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CourierGeolocationProducer {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final StoreCache storeCache;

    @Value("${courier.producer.max-courier-id}")
    private int maxCourierId;

    private final Random random = new Random();
    private boolean storesLoaded = false;
    private List<Store> stores;

    /**
     * Periodically generates and sends courier geolocation messages to the queue.
     * The rate is defined in the application.yml configuration.
     */
    @Scheduled(fixedRateString = "${courier.producer.rate-ms}")
    public void sendCourierGeolocation() {
        if (!initializeStoresIfNeeded()) {
            // If stores aren't loaded yet, skip this round.
            return;
        }

        Store store = selectRandomStore();
        CourierGeolocation geo = createRandomCourierGeolocation(store);

        try {
            String message = objectMapper.writeValueAsString(geo);
            jmsTemplate.convertAndSend(ActiveMQConstants.QUEUE_NAME, message);
            log.debug("Courier geolocation sent to queue: {}", message);
        } catch (Exception e) {
            log.error("An error occurred while producing courier geolocation.", e);
        }
    }

    /**
     * Initializes the stores list if not already done. Returns true if stores are successfully loaded,
     * false otherwise.
     */
    private boolean initializeStoresIfNeeded() {
        if (storesLoaded) {
            return true;
        }

        this.stores = storeCache.getAllStores();
        if (this.stores == null || this.stores.isEmpty()) {
            log.error("No stores available check the store cache.");
            return false;
        } else {
            storesLoaded = true;
            log.info("Successfully loaded {} stores for location generation.", stores.size());
            return true;
        }
    }

    /**
     * Selects a random store from the loaded list of stores.
     */
    private Store selectRandomStore() {
        return stores.get(random.nextInt(stores.size()));
    }

    /**
     * Creates a CourierGeolocation object for a random courier ID, placing the courier
     * approximately within 200 meters of the given store's location.
     *
     * @param store the reference store around which to generate the courier’s location
     * @return a newly constructed CourierGeolocation
     */
    private CourierGeolocation createRandomCourierGeolocation(Store store) {
        // 2km ~ 0.018 degrees; we use ±0.5 * 0.036 to get ±0.018 degrees (~2km)
        double latOffset = (random.nextDouble() - 0.5) * 0.036;
        double lngOffset = (random.nextDouble() - 0.5) * 0.036;

        return CourierGeolocation.builder()
                .courierId((long) (random.nextInt(maxCourierId) + 1))
                .lat(store.getLat() + latOffset)
                .lng(store.getLng() + lngOffset)
                .timestamp(Instant.now())
                .build();
    }
}
