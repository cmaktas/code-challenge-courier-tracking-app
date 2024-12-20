package com.example.couriergeolocationtracker.domain.constants;

import lombok.experimental.UtilityClass;

/**
 * Utility class that holds constants related to ActiveMQ configuration.
 */
@UtilityClass
public class ActiveMQConstants {

    /**
     * The name of the ActiveMQ queue used for courier geolocations.
     */
    public static final String QUEUE_NAME = "courier.geolocations";
}
