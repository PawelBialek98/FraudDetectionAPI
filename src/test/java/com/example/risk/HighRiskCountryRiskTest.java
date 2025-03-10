package com.example.risk;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.service.risk.implementation.HighRiskCountryRisk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HighRiskCountryRiskTest {
    private final HighRiskCountryRisk highRiskCountry = new HighRiskCountryRisk();

    @Test
    void testHighRiskCountryTransaction() {
        TransactionAPI transaction = TransactionAPI.builder()
                .userLocation("AFG")
                .build();
        BinDetails binDetails = BinDetails.builder().build();

        RiskAssessment result = highRiskCountry.evaluate(transaction, binDetails);

        assertEquals(40, result.riskScore());
        assertEquals("Transaction from a high-risk country: AFG", result.assessment());
    }

    @Test
    void testModerateRiskCountryTransaction() {
        TransactionAPI transaction = TransactionAPI.builder()
                .userLocation("BOL")
                .build();
        BinDetails binDetails = BinDetails.builder().build();

        RiskAssessment result = highRiskCountry.evaluate(transaction, binDetails);

        assertEquals(20, result.riskScore());
        assertEquals("Transaction from a moderate risk country: BOL", result.assessment());
    }
}
