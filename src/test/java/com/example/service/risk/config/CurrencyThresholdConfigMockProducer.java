package com.example.service.risk.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.Config;
import com.example.utils.CurrencyThresholdConfig;
import io.smallrye.config.SmallRyeConfig;
import jakarta.inject.Inject;

public class CurrencyThresholdConfigMockProducer {

    @Inject
    Config config;

    @Produces
    @ApplicationScoped
    @io.quarkus.test.Mock
    CurrencyThresholdConfig currencyThresholdConfig() {
        return config.unwrap(SmallRyeConfig.class).getConfigMapping(CurrencyThresholdConfig.class);
    }
}
