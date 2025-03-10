package com.example.service.risk.implementation;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.service.risk.RiskRule;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GovernmentRangeRisk implements RiskRule {

    public RiskAssessment evaluate(TransactionAPI transaction, BinDetails binDetails) {
        if (binDetails.governmentRange()) {
            return new RiskAssessment(100, "Goverment Range Risk");
        }
        return new RiskAssessment(0, "Not Government Range Risk");
    }
}
