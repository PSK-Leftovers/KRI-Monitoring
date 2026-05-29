package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorValueRequest;
import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
import com.leftovers.kri.indicator.dto.IndicatorValues;
import com.leftovers.kri.indicator.thresholds.Thresholds;
import com.leftovers.kri.indicator.thresholds.ThresholdsRepository;
import com.leftovers.kri.notification.IndicatorNotificationService;
import com.leftovers.kri.risk.RiskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorValueService {

    private final IndicatorRepository indicatorRepository;
    private final IndicatorValueRepository indicatorValueRepository;
    private final IndicatorValueMapper indicatorValueMapper;
    private final IndicatorNotificationService indicatorNotificationService;
    private final ThresholdsRepository thresholdsRepository;
    private final RiskService riskService;

    @Transactional
    public IndicatorValueResponse create(Long indicatorId, CreateIndicatorValueRequest request) {
        Indicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new EntityNotFoundException("Indicator not found with id: " + indicatorId));

        IndicatorStatus oldStatus = indicator.getStatus();
        Double oldValue = indicatorValueRepository
                .findTopByIndicatorIdOrderByRecordedAtDesc(indicatorId)
                .map(IndicatorValue::getValue)
                .orElse(null);

        Thresholds thresholds = thresholdsRepository.findTopByIndicatorIdOrderByRecordedAtDesc(indicatorId)
                .orElseThrow(() -> new EntityNotFoundException("Thresholds not found for indicator: " + indicatorId));

        Double newValue = request.value();

        IndicatorStatus newStatus = computeStatus(thresholds, newValue);

        IndicatorValue indicatorValue = new IndicatorValue();
        indicatorValue.setIndicator(indicator);
        indicatorValue.setValue(newValue);

        boolean higherIsBetter = thresholds.getGreenThreshold() > thresholds.getYellowThreshold();
        double riskScore;

        if (higherIsBetter) {
            riskScore = riskService.calculateRiskScore(
                    thresholds.getRedThreshold(),
                    Math.max(newValue, Double.MIN_VALUE)
            );
        } else {
            riskScore = riskService.calculateRiskScore(newValue, thresholds.getRedThreshold());
        }

        log.debug("Strategy applied for indicatorId={}. Direction: higherIsBetter={}, Risk score: {}",
                indicatorId, higherIsBetter, riskScore);

        indicatorValue.setRiskScore(riskScore);

        boolean hadPreviousValue = oldValue != null;

        if (hadPreviousValue && oldStatus != newStatus) {
            indicatorNotificationService.sendNotification(
                    indicator.getName(),
                    indicator.getDescription(),
                    oldStatus,
                    newStatus,
                    oldValue,
                    newValue
            );
        }

        indicator.setStatus(newStatus);
        indicatorRepository.save(indicator);

        return indicatorValueMapper.toResponse(indicatorValueRepository.save(indicatorValue));
    }

    private IndicatorStatus computeStatus(Thresholds thresholds, double value) {
        boolean higherIsBetter = thresholds.getYellowThreshold() > thresholds.getRedThreshold();

        if (higherIsBetter) {
            if (value >= thresholds.getYellowThreshold()) {
                return IndicatorStatus.GREEN;
            }
            if (value >= thresholds.getRedThreshold()) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        } else {
            if (value <= thresholds.getYellowThreshold()) {
                return IndicatorStatus.GREEN;
            }
            if (value <= thresholds.getRedThreshold()) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        }
    }

    public List<IndicatorValues> getIndicatorValues(Long indicatorId, LocalDate from, LocalDate to) {
        Instant fromTimestamp = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant toTimestamp = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return indicatorValueRepository.findByIndicatorIdAndRecordedAtBetweenOrderByRecordedAtAsc(
                indicatorId,
                fromTimestamp,
                toTimestamp
        );
    }
}