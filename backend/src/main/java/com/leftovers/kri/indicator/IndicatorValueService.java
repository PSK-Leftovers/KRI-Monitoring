package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorValueRequest;
import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
import com.leftovers.kri.notification.IndicatorNotificationService;
import com.leftovers.kri.indicator.dto.IndicatorValues;
import com.leftovers.kri.indicator.thresholds.Thresholds;
import com.leftovers.kri.indicator.thresholds.ThresholdsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndicatorValueService {

    private final IndicatorRepository indicatorRepository;
    private final IndicatorValueRepository indicatorValueRepository;
    private final IndicatorValueMapper indicatorValueMapper;
    private final IndicatorNotificationService indicatorNotificationService;
    private final ThresholdsRepository thresholdsRepository;

    @Transactional
    public IndicatorValueResponse create(Long indicatorId, CreateIndicatorValueRequest request) {
        Indicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new EntityNotFoundException("Indicator not found with id: " + indicatorId));

        IndicatorStatus oldStatus = indicator.getStatus();
        Double oldValue = indicatorValueRepository
                .findTopByIndicatorIdOrderByRecordedAtDesc(indicatorId)
                .map(IndicatorValue::getValue)
                .orElse(null);

        IndicatorStatus newStatus = computeStatus(indicator, request.value());
        Double newValue = request.value();

        IndicatorValue indicatorValue = new IndicatorValue();
        indicatorValue.setIndicator(indicator);
        indicatorValue.setValue(newValue);

        boolean hadPreviousValue = oldValue != null;

        if (hadPreviousValue && oldStatus != newStatus) {
            indicatorNotificationService.sendNotification(indicator.getName(), indicator.getDescription(),
                    oldStatus, newStatus, oldValue, newValue);
        }

        indicator.setStatus(newStatus);
        indicatorRepository.save(indicator);

        return indicatorValueMapper.toResponse(indicatorValueRepository.save(indicatorValue));
    }

    private IndicatorStatus computeStatus(Indicator indicator, double value) {
        Thresholds thresholds = thresholdsRepository.findTopByIndicatorIdOrderByRecordedAtDesc(indicator.getId())
            .orElseThrow(() -> new EntityNotFoundException("Thresholds not found for indicator with id: " + indicator.getId()));

        boolean higherIsBetter = thresholds.getGreenThreshold() > thresholds.getYellowThreshold();

        if (higherIsBetter) {
            if (value >= thresholds.getGreenThreshold()) {
                return IndicatorStatus.GREEN;
            }
            if (value >= thresholds.getYellowThreshold()) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        } else {
            if (value <= thresholds.getGreenThreshold()) {
                return IndicatorStatus.GREEN;
            }
            if (value <= thresholds.getYellowThreshold()) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        }
    }

    public List<IndicatorValues> getIndicatorValues(Long indicatorId, LocalDate from, LocalDate to) {

        Instant fromTimestamp = from.atStartOfDay().toInstant(ZoneOffset.UTC);

        Instant toTimestamp = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        return indicatorValueRepository.findByIndicatorIdAndRecordedAtBetweenOrderByRecordedAtAsc(indicatorId, fromTimestamp, toTimestamp);
    }
}
