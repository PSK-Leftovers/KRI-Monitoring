package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import com.leftovers.kri.indicator.dto.UpdateIndicatorRequest;
import com.leftovers.kri.indicator.thresholds.Thresholds;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IndicatorMapper {

    @Mapping(target = "id", source = "indicator.id")
    @Mapping(target = "latestValue", ignore = true)
    @Mapping(target = "version", source = "indicator.version")
    IndicatorResponse toResponse(Indicator indicator, Thresholds thresholds);

    @Mapping(target = "id", source = "indicator.id")
    @Mapping(target = "latestValue", source = "latestValue")
    @Mapping(target = "version", source = "indicator.version")
    IndicatorResponse toResponse(Indicator indicator, Double latestValue, Thresholds thresholds);

    @Mapping(target = "version", ignore = true)
    Indicator toEntity(CreateIndicatorRequest request);

    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(UpdateIndicatorRequest request, @MappingTarget Indicator indicator);
}