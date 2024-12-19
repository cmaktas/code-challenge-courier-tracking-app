package com.example.couriergeolocationtracker.infrastructure.activemq;

import com.example.couriergeolocationtracker.domain.constants.ActiveMQConstants;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;


@EnableJms
@Configuration
public class ActiveMQQueueConfig {

    @Bean
    public ActiveMQQueue courierGeolocationsQueue() {
        return new ActiveMQQueue(ActiveMQConstants.QUEUE_NAME);
    }
}
