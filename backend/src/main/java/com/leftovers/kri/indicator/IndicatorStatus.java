package com.leftovers.kri.indicator;

import lombok.Getter;

@Getter
public enum IndicatorStatus {
    GREEN("Žalia"),
    YELLOW("Geltona"),
    RED("Raudona"),
    UNKNOWN("Nežinoma");

    private final String displayName;

    IndicatorStatus(String displayName) {
        this.displayName = displayName;
    }
}
