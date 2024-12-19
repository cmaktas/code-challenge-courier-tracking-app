package com.example.couriergeolocationtracker.web.model.v1.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourierDistanceResponse {
    private Long courierId;
    private Distance totalDistance;

    @Data
    @Builder
    public static class Distance {
        private double value;
        private String unit;
    }
}
