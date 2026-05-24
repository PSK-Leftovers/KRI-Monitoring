package com.leftovers.kri.indicator.thresholds;

import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;
import com.leftovers.kri.indicator.thresholds.dto.ThresholdChange;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThresholdsService {
    private final ThresholdsRepository thresholdsRepository;
    private final ThresholdsMapper thresholdsMapper;

    public ThresholdsResponse getThresholdChangesByIndicatorId(Long indicatorId, Instant after, Instant before) {
        List<Thresholds> history = thresholdsRepository.findAllByIndicatorIdOrderByRecordedAtDesc(indicatorId);

        history = filterHistory(history, after, before);

        List<ThresholdChange> greenChanges =
            extractChanges(history, Thresholds::getGreenThreshold);
        
        List<ThresholdChange> yellowChanges =
            extractChanges(history, Thresholds::getYellowThreshold);
        
        List<ThresholdChange> redChanges =
            extractChanges(history, Thresholds::getRedThreshold);

        return thresholdsMapper.toDto(greenChanges, yellowChanges, redChanges);
    }

    private List<Thresholds> filterHistory(List<Thresholds> history, Instant after, Instant before) {
        return history
            .stream()
            .dropWhile(thresholds -> {
                return before != null && thresholds.getRecordedAt().isBefore(before);
            })
            .takeWhile(thresholds -> {
                return after != null && thresholds.getRecordedAt().isBefore(after);
            })
            .toList();
    }

    private List<ThresholdChange> extractChanges(List<Thresholds> history, Function<Thresholds, Double> getThresholds) {
        List<ThresholdChange> changes = new ArrayList<>();
        Double previous = null;
        
        for (Thresholds thresholds : history) {
            if (previous == null || previous != getThresholds.apply(thresholds)) {
                changes.add(new ThresholdChange(thresholds.getRecordedAt(), thresholds.getGreenThreshold()));
                previous = getThresholds.apply(thresholds);
            }
        }

        return changes;
    }
}
