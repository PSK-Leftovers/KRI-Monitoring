package com.leftovers.kri.indicator.thresholds.dto;

import java.time.Instant;

public record ThresholdChange(
    Instant recordedAt,
    Double value
) {
}
