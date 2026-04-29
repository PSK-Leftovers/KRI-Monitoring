package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/indicators")
@RequiredArgsConstructor
public class IndicatorController {

    private final IndicatorService indicatorService;

    @GetMapping
    public List<IndicatorResponse> getAll() {
        return indicatorService.getAll();
    }

    @PostMapping
    public IndicatorResponse create(@RequestBody @Valid CreateIndicatorRequest request) {
        return indicatorService.create(request);
    }

    @PutMapping("/{id}")
    public IndicatorResponse update(
            @PathVariable Long id,
            @RequestBody @Valid CreateIndicatorRequest request
    ) {
        return indicatorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        indicatorService.delete(id);
    }
}