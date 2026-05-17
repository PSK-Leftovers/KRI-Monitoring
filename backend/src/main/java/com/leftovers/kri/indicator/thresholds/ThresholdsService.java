package com.leftovers.kri.indicator.thresholds;

import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThresholdsService {
    private final ThresholdsRepository thresholdsRepository;
    private final ThresholdsMapper thresholdsMapper;

    public List<ThresholdsResponse> getAllByIndicatorId(Long indicatorId, Instant after, Instant before) {
        var thresholdsHistory = thresholdsRepository
            .findAllByIndicatorIdOrderByChangedAtDesc(indicatorId)
            .stream();
        
        if (after != null)
            thresholdsHistory.filter(thresholds -> thresholds.getChangedAt().isAfter(after));

        if (before != null)
            thresholdsHistory.filter(thresholds -> thresholds.getChangedAt().isBefore(before));

        return thresholdsHistory
            .map(thresholdsMapper::toDto)
            .toList();
    }
}
