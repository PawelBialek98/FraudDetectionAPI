package com.example.service;

import com.example.model.mastercardApi.BinDetails;
import com.example.model.entity.Transaction;
import com.example.model.api.TransactionAPI;
import com.example.repository.TransactionRepository;
import com.example.service.risk.RiskEvaluator;
import com.example.model.risk.RiskResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class RiskAssessmentService {

    @Inject
    MastercardBinService mastercardBinService;

    @Inject
    RiskEvaluator riskEvaluator;

    @Inject
    TransactionRepository transactionRepository;

    public RiskResult assessRisk(TransactionAPI transaction, String requestId) {
        BinDetails binDetails = mastercardBinService.getBinDetails(transaction.getBinNumber(), requestId)
                .orElseThrow(NotFoundException::new);

        RiskResult riskResult = riskEvaluator.evaluateRisk(transaction, binDetails);
        saveRiskAssessment(transaction, riskResult, requestId);
        return riskResult;
    }

    private void saveRiskAssessment(TransactionAPI transactionAPI, RiskResult riskResult, String requestId) {
        Transaction transaction = new Transaction();
        transaction.setRiskResult(riskResult);
        transaction.setBinNumber(transactionAPI.getBinNumber());
        transaction.setAmount(transactionAPI.getAmount());
        transaction.setCurrency(transactionAPI.getCurrency());
        transaction.setUserLocation(transactionAPI.getUserLocation());
        transaction.setRequestId(requestId);
        transactionRepository.persist(transaction);
    }
}
