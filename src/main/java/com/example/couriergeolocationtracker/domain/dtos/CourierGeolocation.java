package com.example.couriergeolocationtracker.domain.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CourierGeolocation {

    private Long courierId;
    private double lat;
    private double lng;
    private Instant timestamp;

}
