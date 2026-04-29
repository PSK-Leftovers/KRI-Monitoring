package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndicatorService {

    private final IndicatorRepository indicatorRepository;
    private final IndicatorMapper indicatorMapper;

    public List<IndicatorResponse> getAll() {
        return indicatorRepository.findAll().stream()
                .map(indicatorMapper::toResponse)
                .toList();
    }

    public IndicatorResponse create(CreateIndicatorRequest request) {
        Indicator saved = indicatorRepository.save(indicatorMapper.toEntity(request));
        return indicatorMapper.toResponse(saved);
    }

    public IndicatorResponse update(Long id, CreateIndicatorRequest request) {
        Indicator indicator = indicatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indicator not found with id: " + id));

        indicator.setName(request.name());
        indicator.setDescription(request.description());
        indicator.setGreenThreshold(request.greenThreshold());
        indicator.setYellowThreshold(request.yellowThreshold());
        indicator.setRedThreshold(request.redThreshold());

        return indicatorMapper.toResponse(indicatorRepository.save(indicator));
    }

    public void delete(Long id) {
        if (!indicatorRepository.existsById(id)) {
            throw new EntityNotFoundException("Indicator not found with id: " + id);
        }

        indicatorRepository.deleteById(id);
    }
}