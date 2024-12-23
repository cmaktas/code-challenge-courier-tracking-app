package com.example.couriergeolocationtracker.service.consumer.observer.publisher;

import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierEvent;
import com.example.couriergeolocationtracker.service.consumer.observer.observers.CourierEventObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject that publishes courier events to registered observers.
 */
public class CourierEventPublisher {

    private final List<CourierEventObserver> observers = new ArrayList<>();

    public void addObserver(CourierEventObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(CourierEventObserver observer) {
        observers.remove(observer);
    }

    public void publishEvent(CourierEvent event) {
        for (CourierEventObserver observer : observers) {
            observer.onCourierEvent(event);
        }
    }
}
