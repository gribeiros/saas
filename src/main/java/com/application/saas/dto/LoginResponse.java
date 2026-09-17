package com.application.saas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing JWT token details")
public record LoginResponse(
        @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "Token authentication type", example = "Bearer")
        String tokenType,

        @Schema(description = "Token expiration duration in seconds", example = "3600")
        long expiresIn
) {
    public static LoginResponse bearer(String token, long expiresIn) {
        return new LoginResponse(token, "Bearer", expiresIn);
    }
}

