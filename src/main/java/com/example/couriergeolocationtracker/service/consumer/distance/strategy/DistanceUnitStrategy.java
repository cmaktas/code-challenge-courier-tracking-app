package com.example.couriergeolocationtracker.service.consumer.distance.strategy;

/**
 * Interface defining a strategy for converting distances from meters
 * to a specific unit of measurement.
 */
public interface DistanceUnitStrategy {

    /**
     * Converts a distance from meters to the target unit.
     *
     * @param meters The distance in meters.
     * @return The converted distance in the target unit.
     */
    double convert(double meters);

    /**
     * Returns the name of the target unit (e.g., "km" or "mi").
     *
     * @return The name of the target unit.
     */
    String getUnitName();
}
