package com.leftovers.kri.risk;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kri.risk.calculator", havingValue = "threshold", matchIfMissing = true)
public class ThresholdRiskCalculator implements RiskCalculationStrategy {

    @Override
    public double calculateRiskScore(double currentValue, double threshold) {
        if (!Double.isFinite(currentValue) || !Double.isFinite(threshold) || threshold <= 0.0d) {
            throw new IllegalArgumentException("Risk inputs must be finite and threshold must be > 0");
        }

        double ratio = currentValue / threshold;
        double score = ratio * 100.0d;
        return Math.max(0.0d, Math.min(100.0d, score));
    }
}