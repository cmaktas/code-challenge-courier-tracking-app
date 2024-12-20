package com.example.couriergeolocationtracker.infrastructure.activemq;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up an embedded ActiveMQ broker.
 */
@Configuration
public class EmbeddedActiveMQBrokerConfig {

    /**
     * Bean definition for the embedded ActiveMQ broker.
     *
     * @return a configured {@link BrokerService} instance.
     * @throws Exception if there is an error creating the broker.
     */
    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("embedded-broker");
        broker.addConnector("vm://embedded-broker?create=false");
        broker.setUseJmx(false);
        broker.setPersistent(false);
        broker.autoStart();
        return broker;
    }
}
