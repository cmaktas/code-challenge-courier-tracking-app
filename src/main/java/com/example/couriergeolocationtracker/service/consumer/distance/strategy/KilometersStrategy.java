package com.example.couriergeolocationtracker.service.consumer.distance.strategy;

import org.springframework.stereotype.Component;

/**
 * Strategy for converting meters to kilometers.
 */
@Component
public class KilometersStrategy implements DistanceUnitStrategy {

    @Override
    public double convert(double meters) {
        return meters / 1000.0;
    }

    @Override
    public String getUnitName() {
        return "km";
    }
}
