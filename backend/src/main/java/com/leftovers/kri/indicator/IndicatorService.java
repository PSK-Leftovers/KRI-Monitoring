package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import com.leftovers.kri.indicator.dto.UpdateIndicatorRequest;
import com.leftovers.kri.indicator.thresholds.Thresholds;
import com.leftovers.kri.indicator.thresholds.ThresholdsMapper;
import com.leftovers.kri.indicator.thresholds.ThresholdsRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndicatorService {
    private final IndicatorRepository indicatorRepository;
    private final IndicatorValueRepository indicatorValueRepository;
    private final IndicatorMapper indicatorMapper;
    private final ThresholdsMapper thresholdsMapper;
    private final ThresholdsRepository thresholdsRepository;


    @Transactional(readOnly = true)
    public List<IndicatorResponse> getAll() {
        return indicatorRepository.findAll().stream()
                .map(indicator -> {
                    Double latestValue = indicatorValueRepository
                            .findTopByIndicatorIdOrderByRecordedAtDesc(indicator.getId())
                            .map(IndicatorValue::getValue)
                            .orElse(null);

                    Thresholds latestThresholds = thresholdsRepository
                            .findTopByIndicatorIdOrderByRecordedAtDesc(indicator.getId())
                            .orElse(null);

                    return indicatorMapper.toResponse(
                        indicator,
                        latestValue,
                        latestThresholds
                    );
                })
                .toList();
    }

    @Transactional
    public IndicatorResponse create(CreateIndicatorRequest request) {
        Indicator indicator = indicatorRepository.save(indicatorMapper.toEntity(request));
        
        Thresholds thresholds = thresholdsRepository.save(thresholdsMapper.toEntity(request, indicator));

        return indicatorMapper.toResponse(
            indicator,
            thresholds
        );
    }

    @Transactional
    public IndicatorResponse update(Long id, CreateIndicatorRequest request) {
        Indicator indicator = indicatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indicator not found with id: " + id));

        if (request.version() != null && !indicator.getVersion().equals(request.version())) {
            throw new ObjectOptimisticLockingFailureException(Indicator.class, id);
        }

        indicatorMapper.updateEntityFromDto(request, indicator);

        Indicator saved = indicatorRepository.saveAndFlush(indicator);
        Thresholds thresholds = thresholdsRepository.save(thresholdsMapper.toEntity(request, saved));

        return indicatorMapper.toResponse(saved, thresholds);
    }

    @Transactional
    public void delete(Long id) {
        if (!indicatorRepository.existsById(id)) {
            throw new EntityNotFoundException("Indicator not found with id: " + id);
        }

        indicatorRepository.deleteById(id);
    }
}