package com.application.saas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Validation error details for a specific field")
public record FieldErrorDetail(
        @Schema(description = "Name of the invalid field", example = "email")
        String field,

        @Schema(description = "Description of validation failure", example = "Email format is invalid")
        String message
) {}
