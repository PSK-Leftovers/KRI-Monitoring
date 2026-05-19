package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorValueRequest;
import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
import com.leftovers.kri.notification.IndicatorNotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndicatorValueService {

    private final IndicatorRepository indicatorRepository;
    private final IndicatorValueRepository indicatorValueRepository;
    private final IndicatorValueMapper indicatorValueMapper;
    private final IndicatorNotificationService indicatorNotificationService;

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
        Double green = indicator.getGreenThreshold();
        Double yellow = indicator.getYellowThreshold();

        if (green == null || yellow == null) {
            return IndicatorStatus.UNKNOWN;
        }

        boolean higherIsBetter = green > yellow;

        if (higherIsBetter) {
            if (value >= green) {
                return IndicatorStatus.GREEN;
            }
            if (value >= yellow) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        } else {
            if (value <= green) {
                return IndicatorStatus.GREEN;
            }
            if (value <= yellow) {
                return IndicatorStatus.YELLOW;
            }
            return IndicatorStatus.RED;
        }
    }
}
