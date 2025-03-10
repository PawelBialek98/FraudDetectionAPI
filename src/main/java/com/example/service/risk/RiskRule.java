package com.example.service.risk;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;

public interface RiskRule {
    RiskAssessment evaluate(TransactionAPI transaction, BinDetails binDetails);
}