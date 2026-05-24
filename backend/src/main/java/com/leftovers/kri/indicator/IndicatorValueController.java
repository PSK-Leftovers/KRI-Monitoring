package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorValueRequest;
import com.leftovers.kri.indicator.dto.IndicatorValueResponse;
import com.leftovers.kri.indicator.dto.IndicatorValues;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/indicators")
@RequiredArgsConstructor
public class IndicatorValueController {

    private final IndicatorValueService indicatorValueService;

    @PostMapping("/{indicatorId}/values")
    public IndicatorValueResponse create(
            @PathVariable Long indicatorId,
            @RequestBody @Valid CreateIndicatorValueRequest request
    ) {
        return indicatorValueService.create(indicatorId, request);
    }

    @GetMapping("/{indicatorId}/values")
    public List<IndicatorValues> getIndicatorValues(@PathVariable Long indicatorId, @RequestParam LocalDate from, @RequestParam LocalDate to) {

        return indicatorValueService.getIndicatorValues(indicatorId, from, to);
    }

}
