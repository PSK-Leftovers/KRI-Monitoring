package com.leftovers.kri.user.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        String password,

        @NotNull
        @Pattern(regexp = "ANALYST|DIRECTOR|ADMIN")
        String role
) {}
