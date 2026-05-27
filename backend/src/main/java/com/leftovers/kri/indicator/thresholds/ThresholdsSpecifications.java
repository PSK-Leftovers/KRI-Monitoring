package com.leftovers.kri.indicator.thresholds;

import java.time.Instant;

import org.springframework.data.jpa.domain.PredicateSpecification;

import com.leftovers.kri.indicator.Indicator_;

import jakarta.annotation.Nullable;

public class ThresholdsSpecifications {
    static PredicateSpecification<Thresholds> hasIndicatorWithId(Long indicatorId) {
        return (from, builder) -> {
            return builder.equal(from.get(Thresholds_.indicator).get(Indicator_.id), indicatorId);
        };
    }

    static PredicateSpecification<Thresholds> recordedBefore(@Nullable Instant before) {
        return (from, builder) -> {
            return builder.lessThanOrEqualTo(from.get(Thresholds_.recordedAt), before);
        };
    }

    static PredicateSpecification<Thresholds> recordedAfter(@Nullable Instant after) {
        return (from, builder) -> {
            return builder.greaterThanOrEqualTo(from.get(Thresholds_.recordedAt), after);
        };
    }
}
