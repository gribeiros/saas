package com.application.saas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Authenticated user profile summary")
public record UserSummaryResponse(
        @Schema(description = "User unique identifier", example = "3a8c56fa-c918-42f4-8d4b-972179b0bf80")
        UUID id,

        @Schema(description = "Username", example = "lucas.mendonca")
        String username,

        @Schema(description = "Assigned user roles", example = "[\"ROLE_USER\"]")
        Set<String> roles,

        @Schema(description = "Personal data and address list")
        PersonResponse person,

        @Schema(description = "User creation timestamp")
        Instant createdAt
) {}
