package com.application.saas.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn
) {
    public static LoginResponse bearer(String token, long expiresIn) {
        return new LoginResponse(token, "Bearer", expiresIn);
    }
}

