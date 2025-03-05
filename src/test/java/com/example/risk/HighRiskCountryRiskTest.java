package com.example.risk;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.service.risk.implementation.HighRiskCountryRisk;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HighRiskCountryRiskTest {
    private HighRiskCountryRisk highRiskCountry = new HighRiskCountryRisk();

    @Test
    void testHighRiskCountryTransaction() {
        TransactionAPI transaction = Mockito.mock(TransactionAPI.class);
        BinDetails binDetails = Mockito.mock(BinDetails.class);

        Mockito.when(transaction.getUserLocation()).thenReturn("AFG");

        RiskAssessment result = highRiskCountry.evaluate(transaction, binDetails);

        assertEquals(40, result.riskScore());
        assertEquals("Transaction from a high-risk country: AFG", result.assessment());
    }

    @Test
    void testModerateRiskCountryTransaction() {
        TransactionAPI transaction = Mockito.mock(TransactionAPI.class);
        BinDetails binDetails = Mockito.mock(BinDetails.class);

        Mockito.when(transaction.getUserLocation()).thenReturn("BOL");

        RiskAssessment result = highRiskCountry.evaluate(transaction, binDetails);

        assertEquals(20, result.riskScore());
        assertEquals("Transaction from a high-risk country: BOL", result.assessment());
    }
}
