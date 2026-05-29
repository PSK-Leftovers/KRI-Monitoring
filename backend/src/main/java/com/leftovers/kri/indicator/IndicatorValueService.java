package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorValueRequest;
import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
import com.leftovers.kri.logging.Audited;
import com.leftovers.kri.notification.IndicatorNotificationService;
import com.leftovers.kri.indicator.dto.IndicatorValues;
import com.leftovers.kri.notification.IndicatorNotificationService;
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
    private final IndicatorStatusService indicatorStatusService;
    private final IndicatorNotificationService indicatorNotificationService;

    @Audited(action = "RECORD_INDICATOR_VALUE")
    @Transactional
    public IndicatorValueResponse create(Long indicatorId, CreateIndicatorValueRequest request) {
        Indicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new EntityNotFoundException("Indicator not found with id: " + indicatorId));

        IndicatorStatus oldStatus = indicator.getStatus();
        Double oldValue = indicatorValueRepository
                .findTopByIndicatorIdOrderByRecordedAtDesc(indicatorId)
                .map(IndicatorValue::getValue)
                .orElse(null);

        IndicatorStatus newStatus = indicatorStatusService.compute(indicator, request.value());
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

    @Transactional(readOnly = true)
    public List<IndicatorValues> getIndicatorValues(Long indicatorId, LocalDate from, LocalDate to) {

        Instant fromTimestamp = from.atStartOfDay().toInstant(ZoneOffset.UTC);

        Instant toTimestamp = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        return indicatorValueRepository.findByIndicatorIdAndRecordedAtBetweenOrderByRecordedAtAsc(indicatorId, fromTimestamp, toTimestamp);
    }
}
