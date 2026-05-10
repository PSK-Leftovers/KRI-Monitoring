package com.leftovers.kri.indicator.dto;

import java.time.LocalDateTime;

public record IndicatorValueResponse(
        Long id,
        Long indicatorId,
        Double value,
        LocalDateTime recordedAt
) {
}
