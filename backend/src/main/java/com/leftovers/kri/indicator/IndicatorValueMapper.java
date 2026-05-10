package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndicatorValueMapper {

    @Mapping(target = "indicatorId", source = "indicator.id")
    IndicatorValueResponse toResponse(IndicatorValue value);
}
