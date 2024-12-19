package com.example.couriergeolocationtracker.infrastructure.activemq;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedActiveMQBrokerConfig {

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
