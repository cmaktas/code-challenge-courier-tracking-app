package com.example.couriergeolocationtracker.infrastructure.cache;

import com.example.couriergeolocationtracker.domain.entities.Store;
import com.example.couriergeolocationtracker.infrastructure.repository.StoreRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * StoreCache acts as a singleton service holding store data in memory.
 * Data is loaded once the application is ready and does not change at runtime.
 */
@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class StoreCache {

    private final StoreRepository storeRepository;

    /**
     * Instance-level (non-static) list of stores.
     * This ensures each instance of StoreCache (only one due to Spring Singleton) holds the stores data.
     */
    private List<Store> stores;

    /**
     * Load store data into memory once the application is fully ready.
     * This ensures that the database and all initializations have completed, including data.sql.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadStores() {
        stores = storeRepository.findAll();
        if (stores.isEmpty()) {
            log.warn("No stores found in the database. Store-based logic may not work as expected.");
        } else {
            log.info("Loaded {} stores into memory.", stores.size());
        }
    }

    /**
     * Returns the in-memory list of stores.
     */
    public List<Store> getAllStores() {
        return stores;
    }
}
