package com.leftovers.kri.indicator.dto;

import java.time.LocalDateTime;

public record IndicatorValues(
    Double value,
    LocalDateTime recordedAt
) {}
