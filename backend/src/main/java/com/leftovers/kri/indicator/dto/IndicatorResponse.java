package com.leftovers.kri.indicator.dto;

import com.leftovers.kri.indicator.IndicatorStatus;

public record IndicatorResponse(
        Long id,
        String name,
        String description,
        Double greenThreshold,
        Double yellowThreshold,
        Double redThreshold,
        IndicatorStatus status
) {
}