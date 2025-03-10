package com.example.service.risk;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.model.risk.RiskResult;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RiskEvaluator {
    @Inject
    @All
    List<RiskRule> riskRules;

    public RiskResult evaluateRisk(TransactionAPI transaction, BinDetails binDetails) {
        return riskRules.stream()
                .map(rule -> rule.evaluate(transaction, binDetails))
                .filter(assessment -> assessment.riskScore() > 0)
                .collect(Collectors.teeing(
                        Collectors.summingInt(RiskAssessment::riskScore),
                        Collectors.mapping(RiskAssessment::assessment, Collectors.toList()),
                        (totalScore, reasons) -> new RiskResult(Math.min(totalScore, 100), reasons)
                ));
    }
}
