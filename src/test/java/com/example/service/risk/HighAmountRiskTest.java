package com.example.service.risk;

import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.service.risk.implementation.HighAmountRisk;
import com.example.utils.CurrencyThresholdConfig;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *  Testing HighAmountRisk calculation.
 *  Because of using CurrencyThresholdConfig with ConfigMapping this tests need to be run as QuarkusTest
 *  to properly mock CurrencyThresholdConfig
 */
@QuarkusTest
public class HighAmountRiskTest {

    @InjectMock
    CurrencyThresholdConfig currencyThresholdConfig;

    @Inject
    HighAmountRisk risk;

    @Test
    public void normalAmountTransaction() {
        TransactionAPI transaction = TransactionAPI.builder()
                .currency("USD")
                .amount(10000)
                .build();
        BinDetails binDetails = BinDetails.builder().build();

        Mockito.when(currencyThresholdConfig.thresholds()).thenReturn(Map.of("USD", 100000L));

        RiskAssessment result = risk.evaluate(transaction, binDetails);

        assertEquals(0, result.riskScore());
        assertEquals("Amount is normal", result.assessment());
    }

    @Test
    public void highAmountRiskTransaction() {
        TransactionAPI transaction = TransactionAPI.builder()
                .currency("USD")
                .amount(1000000L)
                .build();
        BinDetails binDetails = BinDetails.builder().build();

        Mockito.when(currencyThresholdConfig.thresholds()).thenReturn(Map.of("USD", 100000L));

        RiskAssessment result = risk.evaluate(transaction, binDetails);

        assertEquals(30, result.riskScore());
        assertEquals("High transaction amount", result.assessment());
    }

    @Test
    public void invalidConfigForThresholds() {
        TransactionAPI transaction = TransactionAPI.builder()
                .binNumber("12345678")
                .currency("USD")
                .amount(1000000L)
                .build();
        BinDetails binDetails = BinDetails.builder().build();

        Mockito.when(currencyThresholdConfig.thresholds()).thenReturn(Map.of());

        RiskAssessment result = risk.evaluate(transaction, binDetails);

        assertEquals(20, result.riskScore());
        assertEquals("Cannot evaluate currency for binNumber: " + transaction.binNumber(), result.assessment());
    }

    @Test
    public void invalidTransactionCurrency() {
        TransactionAPI transaction = TransactionAPI.builder()
                .binNumber("12345678")
                .currency("AAA")
                .amount(1000000L)
                .build();
        BinDetails binDetails = BinDetails.builder().build();

        Mockito.when(currencyThresholdConfig.thresholds()).thenReturn(Map.of());

        RiskAssessment result = risk.evaluate(transaction, binDetails);

        assertEquals(20, result.riskScore());
        assertEquals("Cannot evaluate currency for binNumber: " + transaction.binNumber(), result.assessment());
    }
}
