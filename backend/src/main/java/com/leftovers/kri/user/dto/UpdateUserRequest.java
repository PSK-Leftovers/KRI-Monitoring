package com.leftovers.kri.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @Size(min = 8)
        String password,

        @NotNull
        @Pattern(regexp = "ANALYST|DIRECTOR|ADMIN")
        String role
) {}