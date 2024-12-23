package com.example.couriergeolocationtracker.infrastructure.lifecycle;

import com.example.couriergeolocationtracker.service.consumer.DataFlushService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.BrokerService;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationShutdownOrchestrator {

    private final ThreadPoolTaskScheduler producerTaskScheduler;
    private final JmsListenerEndpointRegistry endpointRegistry;
    private final BrokerService brokerService;
    private final DataFlushService dataFlushService;

    public ApplicationShutdownOrchestrator(
            ThreadPoolTaskScheduler producerTaskScheduler,
            JmsListenerEndpointRegistry endpointRegistry,
            BrokerService brokerService,
            DataFlushService dataFlushService) {
        this.producerTaskScheduler = producerTaskScheduler;
        this.endpointRegistry = endpointRegistry;
        this.brokerService = brokerService;
        this.dataFlushService = dataFlushService;
    }

    @PreDestroy
    public void orchestrateShutdown() {
        log.info("=== Initiating graceful shutdown sequence ===");

        log.info("Stopping producer's TaskScheduler...");
        if (producerTaskScheduler != null) {
            producerTaskScheduler.shutdown();
            log.info("Producer TaskScheduler stopped.");
        }

        log.info("Stopping JMS listeners...");
        endpointRegistry.stop();
        log.info("JMS listeners stopped.");

        log.info("Shutting down ActiveMQ broker...");
        try {
            brokerService.stop();
            brokerService.waitUntilStopped();
            log.info("ActiveMQ broker shut down successfully.");
        } catch (Exception e) {
            log.error("Error while shutting down ActiveMQ broker", e);
        }

        log.info("Flushing cache to DB on shutdown...");
        try {
            dataFlushService.flushCacheToDB();
            log.info("Cache flush completed successfully during shutdown.");
        } catch (Exception e) {
            log.error("Error flushing cache data on shutdown", e);
        }

        log.info("=== Graceful shutdown sequence complete ===");
    }
}
