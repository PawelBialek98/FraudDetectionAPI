package com.example.utils;

import io.smallrye.config.ConfigMapping;

import java.util.Map;

/**
 * Configuration mapping for currency thresholds.
 */
@ConfigMapping(prefix = "currency")
public interface CurrencyThresholdConfig {
    /**
     * Maps currency codes to their respective thresholds.
     *
     * @return a map of currency codes and threshold values
     */
    Map<String, Long> thresholds();
}
