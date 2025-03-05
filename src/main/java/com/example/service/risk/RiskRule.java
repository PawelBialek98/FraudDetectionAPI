package com.example.service.risk;

import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.model.api.TransactionAPI;

public interface RiskRule {
    RiskAssessment evaluate(TransactionAPI transaction, BinDetails binDetails);
}