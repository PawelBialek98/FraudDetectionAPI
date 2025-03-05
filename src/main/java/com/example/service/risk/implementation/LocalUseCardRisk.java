package com.example.service.risk.implementation;

import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.model.api.TransactionAPI;
import com.example.service.risk.RiskRule;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocalUseCardRisk implements RiskRule {
    @Override
    public RiskAssessment evaluate(TransactionAPI transaction, BinDetails binDetails) {
        if (binDetails.isLocalUse() && !binDetails.getCountry().getAlpha3().equals(transaction.getUserLocation())) {
            return new RiskAssessment(25, "Local-use card used in a foreign country");
        }
        return new RiskAssessment(0, "Card used in an appropriate region");
    }
}
