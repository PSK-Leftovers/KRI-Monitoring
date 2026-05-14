package com.leftovers.kri.indicator.dto;

import java.time.Instant;

public record IndicatorValueResponse(
        Long id,
        Long indicatorId,
        Double value,
        Instant recordedAt
) {
}
