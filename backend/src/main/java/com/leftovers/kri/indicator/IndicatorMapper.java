package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import com.leftovers.kri.indicator.thresholds.Thresholds;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndicatorMapper {

    @Mapping(target = "id", source = "indicator.id")
    @Mapping(target = "latestValue", ignore = true)
    IndicatorResponse toResponse(Indicator indicator, Thresholds thresholds);

    @Mapping(target = "id", source = "indicator.id")
    @Mapping(target = "latestValue", source = "latestValue")
    IndicatorResponse toResponse(Indicator indicator, Double latestValue, Thresholds thresholds);

    Indicator toEntity(CreateIndicatorRequest request);

    void updateEntityFromDto(CreateIndicatorRequest request, @org.mapstruct.MappingTarget Indicator indicator);
}