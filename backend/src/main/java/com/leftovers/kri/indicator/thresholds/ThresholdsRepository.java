package com.leftovers.kri.indicator.thresholds;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThresholdsRepository extends JpaRepository <Thresholds, Long> {
    Optional<Thresholds> findTopByIndicatorIdOrderByRecordedAtDesc(Long indicatorId);

    List<Thresholds> findAllByIndicatorIdOrderByRecordedAtDesc(Long indicatorId);
}
