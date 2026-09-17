package com.application.saas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Standard API error response payload")
public record ErrorResponse(
        @Schema(description = "Error timestamp")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "HTTP error title", example = "Bad Request")
        String error,

        @Schema(description = "Detailed error message", example = "Validation failed for request payload")
        String message,

        @Schema(description = "Request URI path", example = "/register")
        String path,

        @Schema(description = "List of field validation error details")
        List<FieldErrorDetail> details
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse of(int status, String error, String message, String path, List<FieldErrorDetail> details) {
        return new ErrorResponse(Instant.now(), status, error, message, path, details != null ? details : List.of());
    }
}
