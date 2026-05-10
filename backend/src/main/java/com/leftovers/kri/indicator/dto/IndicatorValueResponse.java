package com.leftovers.kri.indicator.dto;

import java.time.OffsetDateTime;

public record IndicatorValueResponse(
        Long id,
        Long indicatorId,
        Double value,
        OffsetDateTime recordedAt
) {
}
