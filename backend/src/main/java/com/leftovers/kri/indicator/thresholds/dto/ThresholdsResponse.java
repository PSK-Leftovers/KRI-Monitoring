package com.leftovers.kri.indicator.thresholds.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ThresholdsResponse(
    List<ThresholdChange> green,
    List<ThresholdChange> yellow,
    List<ThresholdChange> red
) {
}
