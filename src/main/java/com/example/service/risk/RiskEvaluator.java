package com.example.service.risk;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.model.risk.RiskResult;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;

@ApplicationScoped
public class RiskEvaluator {
    @Inject
    @All
    List<RiskRule> riskRules;

    public RiskResult evaluateRisk(TransactionAPI transaction, BinDetails binDetails) throws InterruptedException {

        try(var scope = new StructuredTaskScope<RiskAssessment>()){

            var riskAssessmentsTask =
                    riskRules.stream()
                        .map(riskRule -> scope.fork(() -> riskRule.evaluate(transaction,binDetails)))
                        .toList();

            scope.join();

            return riskAssessmentsTask.stream()
                            .filter(t -> t.state() == StructuredTaskScope.Subtask.State.SUCCESS)
                            .map(StructuredTaskScope.Subtask::get)
                            .filter(assessment -> assessment.riskScore() > 0)
                            .collect(Collectors.teeing(
                                    Collectors.summingInt(RiskAssessment::riskScore),
                                    Collectors.mapping(RiskAssessment::assessment, Collectors.toList()),
                                    (totalScore, reasons) -> new RiskResult(Math.min(totalScore, 100), reasons)
                            ));
        }
    }
}
