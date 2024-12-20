package com.example.couriergeolocationtracker.utils;

import lombok.experimental.UtilityClass;

/**
 * Utility class for calculating the distance between two geographic points
 * using the Haversine formula.
 */
@UtilityClass
public class HaversineDistanceCalculator {

    /**
     * Calculates the distance between two points on Earth in meters.
     *
     * @param lat1 Latitude of the first point in decimal degrees.
     * @param lon1 Longitude of the first point in decimal degrees.
     * @param lat2 Latitude of the second point in decimal degrees.
     * @param lon2 Longitude of the second point in decimal degrees.
     * @return The distance in meters.
     */
    public double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
