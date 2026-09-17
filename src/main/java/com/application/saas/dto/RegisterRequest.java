package com.application.saas.dto;

import com.application.saas.domain.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record RegisterRequest(
        @NotBlank(message = "Username is required and cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Password is required and cannot be blank")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,

        @NotBlank(message = "Name is required and cannot be blank")
        @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
        String name,

        @NotBlank(message = "Email is required and cannot be blank")
        @Email(message = "Email format is invalid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotEmpty(message = "At least one address must be provided")
        List<@Valid AddressRequest> addresses
) {}
