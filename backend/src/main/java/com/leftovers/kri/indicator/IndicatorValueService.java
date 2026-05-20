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
    private final IndicatorStatusService indicatorStatusService;

    @Transactional
    public IndicatorValueResponse create(Long indicatorId, CreateIndicatorValueRequest request) {
        Indicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new EntityNotFoundException("Indicator not found with id: " + indicatorId));

        IndicatorValue indicatorValue = new IndicatorValue();
        indicatorValue.setIndicator(indicator);
        indicatorValue.setValue(request.value());

        indicator.setStatus(indicatorStatusService.compute(indicator, request.value()));
        indicatorRepository.save(indicator);

        return indicatorValueMapper.toResponse(indicatorValueRepository.save(indicatorValue));
    }
}
