package com.example.couriergeolocationtracker.service.consumer;

public interface DistanceUnitStrategy {
    double convert(double meters);
    String getUnitName();
}
