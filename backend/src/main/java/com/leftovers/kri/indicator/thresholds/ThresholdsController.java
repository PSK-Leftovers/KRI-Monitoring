package com.leftovers.kri.indicator.thresholds;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/indicators/{id}/thresholds")
@RequiredArgsConstructor
public class ThresholdsController {
    private final ThresholdsService thresholdsService;

    @GetMapping
    public List<ThresholdsResponse> getAll(@PathVariable Long id, @RequestParam(name = "after", required = false) Instant after, @RequestParam(name = "before", required = false) Instant before) {
        return thresholdsService.getAllByIndicatorId(id, before, after);
    }
}
