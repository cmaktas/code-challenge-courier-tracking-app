package com.example.couriergeolocationtracker.service.consumer.observer.observers;

import com.example.couriergeolocationtracker.service.consumer.observer.events.CourierEvent;

/**
 * Observer interface that listens to courier-related events
 */
public interface CourierEventObserver {
    void onCourierEvent(CourierEvent event);
}
