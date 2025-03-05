package com.example.service.risk.implementation;

import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.model.api.TransactionAPI;
import com.example.service.risk.RiskRule;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.util.Currency;

@ApplicationScoped
public class HighAmountRisk implements RiskRule {

    @ConfigProperty(name = "fraud.detection.high.risk.amount", defaultValue = "1000")
    long highAmountThreshold;

    @Override
    public RiskAssessment evaluate(TransactionAPI transaction, BinDetails binDetails) {
        Currency currency = Currency.getInstance(transaction.getCurrency());
        BigDecimal amount = new BigDecimal(transaction.getAmount()).movePointLeft(currency.getDefaultFractionDigits());
        if (amount.compareTo(BigDecimal.valueOf(highAmountThreshold)) > 0) {
            return new RiskAssessment(30, "High transaction amount");
        }
        return new RiskAssessment(0, "Amount is normal");
    }
}
