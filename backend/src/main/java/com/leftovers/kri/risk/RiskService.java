package com.leftovers.kri.risk;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskCalculationStrategy riskCalculationStrategy;

    public double calculateRiskScore(double currentValue, double threshold) {
        return riskCalculationStrategy.calculateRiskScore(currentValue, threshold);
    }
}