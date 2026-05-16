package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorValueRequest;
import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
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

    @Transactional
    public IndicatorValueResponse create(Long indicatorId, CreateIndicatorValueRequest request) {
        Indicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new EntityNotFoundException("Indicator not found with id: " + indicatorId));

        IndicatorValue indicatorValue = new IndicatorValue();
        indicatorValue.setIndicator(indicator);
        indicatorValue.setValue(request.value());

        indicator.setStatus(computeStatus(indicator, request.value()));
        indicatorRepository.save(indicator);

        return indicatorValueMapper.toResponse(indicatorValueRepository.save(indicatorValue));
    }

    private IndicatorStatus computeStatus(Indicator indicator, double value) {
        Double green = indicator.getThresholds().getGreen();
        Double yellow = indicator.getThresholds().getYellow();

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
