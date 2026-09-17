package com.application.saas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for user login authentication")
public record LoginRequest(
        @Schema(description = "Registered username", example = "lucas.mendonca")
        @NotBlank(message = "Username is required and cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(description = "User password", example = "SenhaForte#2026")
        @NotBlank(message = "Password is required and cannot be blank")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password
) {}

