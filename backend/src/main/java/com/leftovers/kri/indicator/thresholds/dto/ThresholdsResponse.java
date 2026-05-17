package com.leftovers.kri.indicator.thresholds.dto;

import java.time.Instant;

public record ThresholdsResponse(
    Long id,
    Long indicatorId,
    Double greenThreshold,
    Double yellowThreshold,
    Double redThreshold,
    Instant changedAt
) {
}
