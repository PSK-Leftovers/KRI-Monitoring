package com.leftovers.kri.indicator.thresholds.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ThresholdChange(
    Instant recordedAt,
    Double value
) {
}
