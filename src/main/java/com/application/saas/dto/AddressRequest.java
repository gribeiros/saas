package com.application.saas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Address creation payload following Brazilian standards")
public record AddressRequest(
        @Schema(description = "Street name (logradouro)", example = "Rua Oscar Freire")
        @NotBlank(message = "Street is required and cannot be blank")
        @Size(max = 150, message = "Street cannot exceed 150 characters")
        String street,

        @Schema(description = "Building or house number", example = "1420")
        @NotBlank(message = "Number is required and cannot be blank")
        @Size(max = 20, message = "Number cannot exceed 20 characters")
        String number,

        @Schema(description = "Complement information (e.g., apartment, block)", example = "Apto 82 - Bloco B")
        @Size(max = 100, message = "Complement cannot exceed 100 characters")
        String complement,

        @Schema(description = "Neighborhood (bairro)", example = "Cerqueira César")
        @NotBlank(message = "Neighborhood is required and cannot be blank")
        @Size(max = 100, message = "Neighborhood cannot exceed 100 characters")
        String neighborhood,

        @Schema(description = "City name", example = "São Paulo")
        @NotBlank(message = "City is required and cannot be blank")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @Schema(description = "Brazilian state UF (2 letters)", example = "SP")
        @NotBlank(message = "State is required and cannot be blank")
        @Size(min = 2, max = 2, message = "State must be a 2-letter UF abbreviation")
        String state,

        @Schema(description = "Brazilian postal code (CEP)", example = "01426-001")
        @NotBlank(message = "Zip code is required and cannot be blank")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Zip code must be a valid Brazilian CEP format (e.g. 01001-000)")
        String zipCode
) {}

