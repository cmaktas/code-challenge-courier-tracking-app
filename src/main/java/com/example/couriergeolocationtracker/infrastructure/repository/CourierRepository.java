package com.example.couriergeolocationtracker.infrastructure.repository;

import com.example.couriergeolocationtracker.domain.entities.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {
}
