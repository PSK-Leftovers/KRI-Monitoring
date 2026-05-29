package com.leftovers.kri.risk;

public interface RiskCalculationStrategy {
    double calculateRiskScore(double currentValue, double threshold);
}