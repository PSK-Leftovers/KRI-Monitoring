package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndicatorMapper {

    IndicatorResponse toResponse(Indicator indicator);

    Indicator toEntity(CreateIndicatorRequest request);

    void updateEntityFromDto(CreateIndicatorRequest request, @org.mapstruct.MappingTarget Indicator indicator);
}