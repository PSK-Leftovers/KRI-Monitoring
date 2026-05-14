package com.leftovers.kri.indicator.dto;

import java.time.Instant;

public record IndicatorValues(
    Double value,
    Instant recordedAt
) {}
