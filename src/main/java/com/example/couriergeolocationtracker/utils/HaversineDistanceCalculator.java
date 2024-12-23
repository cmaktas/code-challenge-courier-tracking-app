package com.example.couriergeolocationtracker.utils;

import lombok.experimental.UtilityClass;

/**
 * Utility class for calculating the distance between two geographic points
 * using the Haversine formula.
 */
@UtilityClass
public class HaversineDistanceCalculator {

    /**
     * Calculates the distance between two geographic coordinates in meters.
     *
     * @param latitude1  Latitude of the first coordinate in decimal degrees.
     * @param longitude1 Longitude of the first coordinate in decimal degrees.
     * @param latitude2  Latitude of the second coordinate in decimal degrees.
     * @param longitude2 Longitude of the second coordinate in decimal degrees.
     * @return The distance in meters.
     */
    public double calculateDistanceInMeters(double latitude1, double longitude1, double latitude2, double longitude2) {
        double earthRadiusMeters = 6371000; // Earth's radius in meters
        double deltaLatitude = Math.toRadians(latitude2 - latitude1);
        double deltaLongitude = Math.toRadians(longitude2 - longitude1);
        double haversineComponent = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2) +
            Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);
        double centralAngle = 2 * Math.atan2(Math.sqrt(haversineComponent), Math.sqrt(1 - haversineComponent));
        return earthRadiusMeters * centralAngle;
    }
}
