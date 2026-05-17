package com.leftovers.kri.indicator.thresholds;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.leftovers.kri.indicator.Indicator;
import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;

@Mapper(componentModel = "spring")
public interface ThresholdsMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "indicator", ignore = true)
    @Mapping(target = "changedAt", ignore = true)
    Thresholds toEntity(CreateIndicatorRequest request, Indicator indicator);

    @Mapping(target = "indicatorId", source = "indicator.id")
    ThresholdsResponse toDto(Thresholds thresholds);
}
