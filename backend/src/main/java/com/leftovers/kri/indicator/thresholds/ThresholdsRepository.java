package com.leftovers.kri.indicator.thresholds;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ThresholdsRepository extends JpaRepository <Thresholds, Long>, JpaSpecificationExecutor<Thresholds> {
    Optional<Thresholds> findTopByIndicatorIdOrderByRecordedAtDesc(Long indicatorId);
}
