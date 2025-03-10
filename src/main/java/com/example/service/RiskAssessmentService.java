package com.example.service;

import com.example.logging.Logged;
import com.example.model.api.TransactionAPI;
import com.example.model.entity.Transaction;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskResult;
import com.example.repository.TransactionRepository;
import com.example.service.risk.RiskEvaluator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.MDC;

import static com.example.utils.Constants.REQUEST_ID_HEADER;

@Logged
@ApplicationScoped
public class RiskAssessmentService {

    @Inject
    MastercardBinService mastercardBinService;

    @Inject
    RiskEvaluator riskEvaluator;

    @Inject
    TransactionRepository transactionRepository;

    /**
     * Assess Risk for given transaction
     *
     * @param transaction to evaluate risk of
     * @return RiskResult with score and description of potential risk
     */
    public RiskResult assessRisk(TransactionAPI transaction) {
        BinDetails binDetails = mastercardBinService.getBinDetails(transaction.binNumber());

        RiskResult riskResult = riskEvaluator.evaluateRisk(transaction, binDetails);
        saveRiskAssessment(transaction, riskResult);
        return riskResult;
    }

    /**
     * Save calculated transaction risk to DB
     *
     * @param transactionAPI incoming transactionAPI
     * @param riskResult     calculated risk for transaction
     */
    private void saveRiskAssessment(TransactionAPI transactionAPI, RiskResult riskResult) {
        Transaction transaction = Transaction.builder()
                .riskResult(riskResult)
                .binNumber(transactionAPI.binNumber())
                .amount(transactionAPI.amount())
                .currency(transactionAPI.currency())
                .userLocation(transactionAPI.userLocation())
                .requestId(MDC.get(REQUEST_ID_HEADER))
                .build();

        transactionRepository.persist(transaction);
    }
}
