package com.leftovers.kri.indicator.dto;

public record IndicatorResponse(
        Long id,
        String name,
        String description,
        Double greenThreshold,
        Double yellowThreshold,
        Double redThreshold
) {
}