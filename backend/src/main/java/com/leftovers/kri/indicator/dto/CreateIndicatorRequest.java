package com.leftovers.kri.indicator.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateIndicatorRequest(
        @NotBlank
        String name,
        String description,
        Double greenThreshold,
        Double yellowThreshold,
        Double redThreshold
) {
}