package com.application.saas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Street is required and cannot be blank")
        @Size(max = 150, message = "Street cannot exceed 150 characters")
        String street,

        @NotBlank(message = "Number is required and cannot be blank")
        @Size(max = 20, message = "Number cannot exceed 20 characters")
        String number,

        @Size(max = 100, message = "Complement cannot exceed 100 characters")
        String complement,

        @NotBlank(message = "Neighborhood is required and cannot be blank")
        @Size(max = 100, message = "Neighborhood cannot exceed 100 characters")
        String neighborhood,

        @NotBlank(message = "City is required and cannot be blank")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @NotBlank(message = "State is required and cannot be blank")
        @Size(min = 2, max = 2, message = "State must be a 2-letter UF abbreviation")
        String state,

        @NotBlank(message = "Zip code is required and cannot be blank")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Zip code must be a valid Brazilian CEP format (e.g. 01001-000)")
        String zipCode
) {}

