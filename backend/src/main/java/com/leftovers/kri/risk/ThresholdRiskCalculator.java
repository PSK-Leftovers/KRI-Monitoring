package com.leftovers.kri.risk;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name = "kri.risk.calculator", havingValue = "threshold", matchIfMissing = true)
public class ThresholdRiskCalculator implements RiskCalculationStrategy {
    @Override
    public double calculateRiskScore(double currentValue, double threshold) {
        if (threshold == 0.0d) {
            return currentValue == 0.0d ? 0.0d : 100.0d;
        }

        double ratio = currentValue / threshold;
        return Math.max(0.0d, ratio * 100.0d);
    }
}