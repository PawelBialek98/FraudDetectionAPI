package com.example.service.risk.implementation;

import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskAssessment;
import com.example.model.api.TransactionAPI;
import com.example.service.risk.RiskRule;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class HighRiskCountryRisk implements RiskRule {
    public static final Set<String> HIGH_RISK_COUNTRIES = Set.of(
            "AFG", "BLR", "BDI", "CAF", "COD", "CUB", "ERI", "HTI", "IRN", "IRQ",
            "PRK", "LBN", "LBY", "MLI", "MOZ", "MMR", "NIC", "PAK", "PSE", "RUS",
            "SOM", "SDN", "SSD", "SYR", "VEN", "YEM", "ZWE"
    );

    public static final Set<String> MODERATE_RISK_COUNTRIES = Set.of(
            "DZA", "BGD", "BOL", "EGY", "ETH", "GTM", "GIN", "HND", "KAZ", "KEN",
            "KGZ", "LAO", "MDG", "MRT", "NGA", "SEN", "LKA", "TJK", "TZA", "TKM",
            "UGA", "UZB", "ZMB"
    );

    @Override
    public RiskAssessment evaluate(TransactionAPI transaction, BinDetails binDetails) {
        if (HIGH_RISK_COUNTRIES.contains(transaction.getUserLocation())) {
            return new RiskAssessment(40, "Transaction from a high-risk country");
        } else if (MODERATE_RISK_COUNTRIES.contains(transaction.getUserLocation())) {
            return new RiskAssessment(20, "Transaction from a moderate risk country");
        }
        return new RiskAssessment(0, "Country is safe");
    }
}
