package com.leftovers.kri.indicator;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndicatorValueRepository extends JpaRepository<IndicatorValue, Long> {
    Optional<IndicatorValue> findTopByIndicatorIdOrderByRecordedAtDesc(Long indicatorId);
}
