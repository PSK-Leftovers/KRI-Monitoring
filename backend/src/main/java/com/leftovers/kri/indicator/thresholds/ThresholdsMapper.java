package com.leftovers.kri.indicator.thresholds;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.leftovers.kri.indicator.Indicator;
import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.UpdateIndicatorRequest;
import com.leftovers.kri.indicator.thresholds.dto.ThresholdChange;
import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;

@Mapper(componentModel = "spring")
public interface ThresholdsMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recordedAt", ignore = true)
    Thresholds toEntity(CreateIndicatorRequest request, Indicator indicator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recordedAt", ignore = true)
    Thresholds toEntity(UpdateIndicatorRequest request, Indicator indicator);

    ThresholdsResponse toDto(List<ThresholdChange> green, List<ThresholdChange> yellow, List<ThresholdChange> red);
}
