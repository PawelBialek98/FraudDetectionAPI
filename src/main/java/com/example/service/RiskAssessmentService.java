package com.example.service;

import com.example.model.mastercardApi.BinDetails;
import com.example.model.entity.Transaction;
import com.example.model.api.TransactionAPI;
import com.example.repository.TransactionRepository;
import com.example.service.risk.RiskEvaluator;
import com.example.model.risk.RiskResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.MDC;

import static com.example.utils.Constants.REQUEST_ID_HEADER;

@ApplicationScoped
public class RiskAssessmentService {

    @Inject
    MastercardBinService mastercardBinService;

    @Inject
    RiskEvaluator riskEvaluator;

    @Inject
    TransactionRepository transactionRepository;

    public RiskResult assessRisk(TransactionAPI transaction) {
        BinDetails binDetails = mastercardBinService.getBinDetails(transaction.getBinNumber());

        RiskResult riskResult = riskEvaluator.evaluateRisk(transaction, binDetails);
        saveRiskAssessment(transaction, riskResult);
        return riskResult;
    }

    private void saveRiskAssessment(TransactionAPI transactionAPI, RiskResult riskResult) {
        Transaction transaction = new Transaction();
        transaction.setRiskResult(riskResult);
        transaction.setBinNumber(transactionAPI.getBinNumber());
        transaction.setAmount(transactionAPI.getAmount());
        transaction.setCurrency(transactionAPI.getCurrency());
        transaction.setUserLocation(transactionAPI.getUserLocation());
        transaction.setRequestId(MDC.get(REQUEST_ID_HEADER));
        transactionRepository.persist(transaction);
    }
}
