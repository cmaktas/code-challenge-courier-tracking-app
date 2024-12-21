package com.example.couriergeolocationtracker.service.consumer.distance.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory that returns the appropriate distance unit strategy
 * based on a given unit name.
 */
@Component
public class DistanceUnitStrategyFactory {

    private final Map<String, DistanceUnitStrategy> strategies;

    public DistanceUnitStrategyFactory(KilometersStrategy kmStrategy, MilesStrategy milesStrategy) {
        this.strategies = Map.of(
                "km", kmStrategy,
                "mi", milesStrategy
        );
    }

    /**
     * Returns the strategy for the requested unit.
     * Defaults to kilometers if the requested unit is unknown.
     *
     * @param unitParam The requested unit (e.g., "km" or "mi")
     * @return The corresponding DistanceUnitStrategy
     */
    public DistanceUnitStrategy getStrategy(String unitParam) {
        return strategies.getOrDefault(unitParam.toLowerCase(), strategies.get("km"));
    }
}
