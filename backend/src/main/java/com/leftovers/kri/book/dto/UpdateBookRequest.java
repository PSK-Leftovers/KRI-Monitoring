package com.leftovers.kri.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateBookRequest(
        @NotBlank String title,
        @NotBlank String author,
        @Min(1) @Max(2100) Integer publishedYear
) {}
