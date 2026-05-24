package com.leftovers.kri.indicator.thresholds;

import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;

import jakarta.annotation.Nullable;

import com.leftovers.kri.indicator.thresholds.dto.ThresholdChange;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThresholdsService {
    private final ThresholdsRepository thresholdsRepository;
    private final ThresholdsMapper thresholdsMapper;

    public ThresholdsResponse getThresholdChangesByIndicatorId(Long indicatorId, @Nullable Instant after, @Nullable Instant before) {
        PredicateSpecification<Thresholds> query = ThresholdsSpecifications.hasIndicatorWithId(indicatorId);

        if (after != null) {
            query = query.and(ThresholdsSpecifications.recordedAfter(after));
        }

        if (before != null) {
            query = query.and(ThresholdsSpecifications.recordedBefore(before));
        }
        
        List<Thresholds> history = thresholdsRepository.findAll(query);

        List<ThresholdChange> greenChanges =
            extractChanges(history, Thresholds::getGreenThreshold);
        
        List<ThresholdChange> yellowChanges =
            extractChanges(history, Thresholds::getYellowThreshold);
        
        List<ThresholdChange> redChanges =
            extractChanges(history, Thresholds::getRedThreshold);

        return thresholdsMapper.toDto(greenChanges, yellowChanges, redChanges);
    }

    private List<ThresholdChange> extractChanges(List<Thresholds> history, Function<Thresholds, Double> getThresholds) {
        List<ThresholdChange> changes = new ArrayList<>();
        Double previous = null;
        
        for (Thresholds thresholds : history) {
            Double current = getThresholds.apply(thresholds);

            if (previous == null || Double.compare(previous, current) != 0){
                changes.add(new ThresholdChange(thresholds.getRecordedAt(), current));
                previous = current;
            }
        }

        return changes;
    }
}
