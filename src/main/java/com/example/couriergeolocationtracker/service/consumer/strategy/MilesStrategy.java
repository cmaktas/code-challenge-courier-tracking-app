package com.example.couriergeolocationtracker.service.consumer.strategy;

import org.springframework.stereotype.Component;

/**
 * Strategy for converting meters to miles.
 * 1 mile ≈ 1609.34 meters
 */
@Component
public class MilesStrategy implements DistanceUnitStrategy {

    @Override
    public double convert(double meters) {
        return meters / 1609.34;
    }

    @Override
    public String getUnitName() {
        return "mi";
    }
}
