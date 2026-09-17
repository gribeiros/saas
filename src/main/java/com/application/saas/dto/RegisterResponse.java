package com.application.saas.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String username,
        String email,
        LocalDate birthDate,
        Set<String> roles,
        Instant createdAt
) {}

