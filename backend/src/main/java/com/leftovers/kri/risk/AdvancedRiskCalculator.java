package com.leftovers.kri.risk;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "kri.risk.calculator", havingValue = "advanced")
public class AdvancedRiskCalculator implements RiskCalculationStrategy {
    @Override
    public double calculateRiskScore(double currentValue, double threshold) {
        if (threshold <= 0.0d) {
            return currentValue <= 0.0d ? 0.0d : 100.0d;
        }

        double normalizedGap = (currentValue - threshold) / threshold;
        double amplifiedScore = Math.pow(Math.abs(normalizedGap), 1.5d) * 100.0d;

        if (normalizedGap < 0.0d) {
            return amplifiedScore * 0.5d;
        }
        if (normalizedGap == 0.0d) {
            return 50.0d;
        }
        return Math.min(100.0d, 50.0d + amplifiedScore);
    }
}