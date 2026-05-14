package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndicatorMapper {

    @Mapping(target = "latestValue", ignore = true)
    IndicatorResponse toResponse(Indicator indicator);

    @Mapping(target = "latestValue", source = "latestValue")
    IndicatorResponse toResponse(Indicator indicator, Double latestValue);

    Indicator toEntity(CreateIndicatorRequest request);

    void updateEntityFromDto(CreateIndicatorRequest request, @org.mapstruct.MappingTarget Indicator indicator);
}