package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.dto.CreateIndicatorRequest;
import com.leftovers.kri.indicator.dto.IndicatorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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