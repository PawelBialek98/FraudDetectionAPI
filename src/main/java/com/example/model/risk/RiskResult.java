package com.example.model.risk;

import java.util.List;

public record RiskResult(int totalRiskScore, List<String> reasons) {}
