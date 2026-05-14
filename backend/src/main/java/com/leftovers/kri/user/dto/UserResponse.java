package com.leftovers.kri.user.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        Instant createdAt,
        Instant lastLogin
) {}
