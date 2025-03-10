package com.example.model.mastercardApi;

import lombok.Builder;

@Builder
public record BinDetails(
        String lowAccountRange,
        String highAccountRange,
        String binNum,
        int binLength,
        String acceptanceBrand,
        String ica,
        String customerName,
        Country country,
        boolean localUse,
        boolean authorizationOnly,
        String productCode,
        String productDescription,
        boolean governmentRange,
        boolean nonReloadableIndicator,
        String anonymousPrepaidIndicator,
        String programName,
        String vertical,
        String fundingSource,
        String consumerType,
        String cardholderCurrencyIndicator,
        String billingCurrencyDefault,
        String comboCardIndicator,
        String flexCardIndicator,
        boolean smartDataEnabled,
        String affiliate,
        String credentialStatus,
        String paymentAccountType
) {
    public record Country(String code, String alpha3, String name) {
    }
}
