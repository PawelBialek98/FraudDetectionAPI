package com.example.service.risk.implementation;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.service.risk.RiskRule;
import com.example.utils.CurrencyThresholdConfig;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.Currency;


@ApplicationScoped
public class HighAmountRisk implements RiskRule {

    @Inject
    CurrencyThresholdConfig currencyThresholdConfig;

    @Override
    public RiskAssessment evaluate(TransactionAPI transaction, BinDetails binDetails) {
        Currency currency;
        BigDecimal threshold;
        try {
            currency = Currency.getInstance(transaction.currency());
            threshold = loadThresholdFromConfig(currency);
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.error("Cannot evaluate currency for currency: " + transaction.currency(), e);
            return new RiskAssessment(20, "Cannot evaluate currency for binNumber: " + transaction.binNumber());
        }
        BigDecimal amount = new BigDecimal(transaction.amount()).movePointLeft(currency.getDefaultFractionDigits());
        if (amount.compareTo(threshold) > 0) {
            return new RiskAssessment(30, "High transaction amount");
        }
        return new RiskAssessment(0, "Amount is normal");
    }

    private BigDecimal loadThresholdFromConfig(Currency currency) throws NullPointerException {
        return new BigDecimal(currencyThresholdConfig.thresholds().get(currency.getCurrencyCode())).movePointLeft(currency.getDefaultFractionDigits());
    }

}
