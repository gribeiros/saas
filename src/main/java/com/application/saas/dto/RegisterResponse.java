package com.application.saas.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String username,
        Set<String> roles,
        PersonResponse person,
        Instant createdAt
) {}
