package com.example.service.risk;

import com.example.model.risk.RiskResult;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.model.api.TransactionAPI;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RiskEvaluator {
    @Inject
    @All
    List<RiskRule> riskRules;

    public RiskResult evaluateRisk(TransactionAPI transaction, BinDetails binDetails) {
        int totalRiskScore = 0;
        List<String> riskReasons = new ArrayList<>();

        for (RiskRule rule : riskRules) {
            RiskAssessment assessment = rule.evaluate(transaction, binDetails);
            if (assessment.riskScore() > 0) {
                totalRiskScore += assessment.riskScore();
                riskReasons.add(assessment.assessment());
            }
        }

        return new RiskResult(Math.min(totalRiskScore, 100), riskReasons);
    }
}
