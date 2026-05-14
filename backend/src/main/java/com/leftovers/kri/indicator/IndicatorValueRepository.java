package com.leftovers.kri.indicator;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leftovers.kri.indicator.dto.IndicatorValues;

import java.util.List;
import java.util.Optional;

public interface IndicatorValueRepository extends JpaRepository<IndicatorValue, Long> {
    Optional<IndicatorValue> findTopByIndicatorIdOrderByRecordedAtDesc(Long indicatorId);

    List<IndicatorValues> findByIndicatorIdOrderByRecordedAtAsc(Long indicatorId);
}
