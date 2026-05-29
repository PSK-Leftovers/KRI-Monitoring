package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.thresholds.Thresholds;
import com.leftovers.kri.indicator.thresholds.ThresholdsRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndicatorStatusService {

    private final ThresholdsRepository thresholdsRepository;

    public IndicatorStatus compute(Indicator indicator, double value) {
        Thresholds thresholds = thresholdsRepository.findTopByIndicatorIdOrderByRecordedAtDesc(indicator.getId())
            .orElseThrow(() -> new EntityNotFoundException("Thresholds not found for indicator with id: " + indicator.getId()));

        boolean higherIsBetter = thresholds.getYellowThreshold() > thresholds.getRedThreshold();

        if (higherIsBetter) {
            if (value >= thresholds.getYellowThreshold()) {
                return IndicatorStatus.GREEN;
            }
            if (value >= thresholds.getRedThreshold()) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        } else {
            if (value <= thresholds.getYellowThreshold()) {
                return IndicatorStatus.GREEN;
            }
            if (value <= thresholds.getRedThreshold()) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        }
    }
}
