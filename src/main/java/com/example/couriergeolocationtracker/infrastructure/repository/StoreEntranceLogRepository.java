package com.example.couriergeolocationtracker.infrastructure.repository;

import com.example.couriergeolocationtracker.domain.entities.StoreEntranceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreEntranceLogRepository extends JpaRepository<StoreEntranceLog, Long> {
}
