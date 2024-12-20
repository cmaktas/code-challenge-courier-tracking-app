package com.example.couriergeolocationtracker.infrastructure.activemq;

import com.example.couriergeolocationtracker.domain.constants.ActiveMQConstants;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

/**
 * Configuration class for defining ActiveMQ-related beans.
 */
@EnableJms
@Configuration
public class ActiveMQQueueConfig {

    /**
     * Bean definition for the ActiveMQ queue.
     *
     * @return an {@link ActiveMQQueue} instance configured with the queue name.
     */
    @Bean
    public ActiveMQQueue courierGeolocationsQueue() {
        return new ActiveMQQueue(ActiveMQConstants.QUEUE_NAME);
    }
}
