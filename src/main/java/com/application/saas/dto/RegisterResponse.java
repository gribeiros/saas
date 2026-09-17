package com.application.saas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Response returned after successful user registration")
public record RegisterResponse(
        @Schema(description = "User unique identifier", example = "3a8c56fa-c918-42f4-8d4b-972179b0bf80")
        UUID id,

        @Schema(description = "Registered username", example = "lucas.mendonca")
        String username,

        @Schema(description = "Assigned security roles", example = "[\"ROLE_USER\"]")
        Set<String> roles,

        @Schema(description = "Associated personal data and addresses")
        PersonResponse person,

        @Schema(description = "Account creation timestamp")
        Instant createdAt
) {}
