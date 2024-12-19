package com.example.couriergeolocationtracker.infrastructure.repository;

import com.example.couriergeolocationtracker.domain.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
