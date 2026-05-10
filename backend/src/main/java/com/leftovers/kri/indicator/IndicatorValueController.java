package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorValueRequest;
import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/indicators/{indicatorId}/values")
@RequiredArgsConstructor
public class IndicatorValueController {

    private final IndicatorValueService indicatorValueService;

    @PostMapping
    public IndicatorValueResponse create(
            @PathVariable Long indicatorId,
            @RequestBody @Valid CreateIndicatorValueRequest request
    ) {
        return indicatorValueService.create(indicatorId, request);
    }
}
