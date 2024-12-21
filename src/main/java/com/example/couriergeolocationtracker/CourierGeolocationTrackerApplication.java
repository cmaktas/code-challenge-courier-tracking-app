package com.example.couriergeolocationtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CourierGeolocationTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourierGeolocationTrackerApplication.class, args);
    }

}
