package com.leftovers.kri.indicator.thresholds;

import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;
import com.leftovers.kri.indicator.thresholds.dto.ThresholdChange;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThresholdsService {
    private final ThresholdsRepository thresholdsRepository;
    private final ThresholdsMapper thresholdsMapper;

    public ThresholdsResponse getThresholdChangesByIndicatorId(Long indicatorId, Instant after, Instant before) {
        Stream<Thresholds> thresholdsHistory = thresholdsRepository
            .findAllByIndicatorIdOrderByChangedAtDesc(indicatorId)
            .stream();
        
        if (after != null)
            thresholdsHistory.filter(thresholds -> thresholds.getRecordedAt().isAfter(after));

        if (before != null)
            thresholdsHistory.filter(thresholds -> thresholds.getRecordedAt().isBefore(before));
        
        List<ThresholdChange> greenChanges = new ArrayList<>(), yellowChanges = new ArrayList<>(), redChanges = new ArrayList<>();
        Double greenPrevious = null, yellowPrevious = null, redPrevious = null;

        for (Thresholds thresholds : thresholdsHistory.toList()) {
            if (greenPrevious == null || greenPrevious != thresholds.getGreenThreshold()) {
                greenChanges.add(new ThresholdChange(thresholds.getRecordedAt(), thresholds.getGreenThreshold()));
                greenPrevious = thresholds.getGreenThreshold();
            }

            if (yellowPrevious == null || yellowPrevious != thresholds.getYellowThreshold()) {
                yellowChanges.add(new ThresholdChange(thresholds.getRecordedAt(), thresholds.getYellowThreshold()));
                yellowPrevious = thresholds.getYellowThreshold();
            }

            if (redPrevious == null || redPrevious != thresholds.getRedThreshold()) {
                redChanges.add(new ThresholdChange(thresholds.getRecordedAt(), thresholds.getRedThreshold()));
                redPrevious = thresholds.getRedThreshold();
            }
        }

        return thresholdsMapper.toDto(greenChanges, yellowChanges, redChanges);
    }
}
