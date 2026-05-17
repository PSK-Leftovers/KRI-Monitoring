package com.leftovers.kri.indicator.thresholds.dto;

import java.util.List;

public record ThresholdsResponse(
    List<ThresholdChange> green,
    List<ThresholdChange> yellow,
    List<ThresholdChange> red
) {
}
