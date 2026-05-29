package com.leftovers.kri.risk;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kri.risk.calculator", havingValue = "advanced")
public class AdvancedRiskCalculator implements RiskCalculationStrategy {

    @Override
    public double calculateRiskScore(double currentValue, double threshold) {
        validateInputs(currentValue, threshold);

        double red = threshold;
        double yellow = red * 0.8d;
        double green = red * 0.5d;

        double score;
        if (currentValue <= green) {
            score = (currentValue / green) * 33.33d;
        } else if (currentValue <= yellow) {
            double gap = currentValue - green;
            double range = yellow - green;
            score = 33.33d + ((gap / range) * 33.33d);
        } else if (currentValue <= red) {
            double gap = currentValue - yellow;
            double range = red - yellow;
            score = 66.66d + ((gap / range) * 33.34d);
        } else {
            score = 100.0d;
        }

        return Math.max(0.0d, Math.min(100.0d, score));
    }

    private static void validateInputs(double currentValue, double threshold) {
        if (!Double.isFinite(currentValue) || !Double.isFinite(threshold) || threshold <= 0.0d) {
            throw new IllegalArgumentException("Risk inputs must be finite and threshold must be > 0");
        }
    }
}