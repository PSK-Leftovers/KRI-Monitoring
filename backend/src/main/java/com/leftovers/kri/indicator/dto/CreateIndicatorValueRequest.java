package com.leftovers.kri.indicator.dto;

import jakarta.validation.constraints.NotNull;

public record CreateIndicatorValueRequest(
        @NotNull
        Double value
) {
}
