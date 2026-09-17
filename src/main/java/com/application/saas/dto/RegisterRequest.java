package com.application.saas.dto;

import com.application.saas.domain.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Payload for registering a new user account with personal data and addresses")
public record RegisterRequest(
        @Schema(description = "Unique username for authentication", example = "lucas.mendonca")
        @NotBlank(message = "Username is required and cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(description = "User password (at least 6 characters)", example = "SenhaForte#2026")
        @NotBlank(message = "Password is required and cannot be blank")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,

        @Schema(description = "Full name of the person", example = "Lucas Mendonça de Alencar")
        @NotBlank(message = "Name is required and cannot be blank")
        @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
        String name,

        @Schema(description = "Unique email address", example = "lucas.mendonca@gmail.com")
        @NotBlank(message = "Email is required and cannot be blank")
        @Email(message = "Email format is invalid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @Schema(description = "Birth date in ISO format (yyyy-MM-dd)", example = "1993-04-18")
        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @Schema(description = "Gender (MASCULINO, FEMININO, NAO_INFORMADO)", example = "MASCULINO")
        @NotNull(message = "Gender is required")
        Gender gender,

        @Schema(description = "List of addresses (at least one address is required)")
        @NotEmpty(message = "At least one address must be provided")
        List<@Valid AddressRequest> addresses
) {}
