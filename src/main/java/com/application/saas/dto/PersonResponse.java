package com.application.saas.dto;

import com.application.saas.domain.Gender;
import com.application.saas.domain.Person;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Person details and associated addresses")
public record PersonResponse(
        @Schema(description = "Person unique identifier", example = "e4f3a74b-e847-4927-91fa-40f4628f86f7")
        UUID id,

        @Schema(description = "Full name", example = "Lucas Mendonça de Alencar")
        String name,

        @Schema(description = "Email address", example = "lucas.mendonca@gmail.com")
        String email,

        @Schema(description = "Birth date (yyyy-MM-dd)", example = "1993-04-18")
        LocalDate birthDate,

        @Schema(description = "Gender", example = "MASCULINO")
        Gender gender,

        @Schema(description = "List of associated addresses")
        List<AddressResponse> addresses,

        @Schema(description = "Timestamp when the person record was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the person record was last updated")
        Instant updatedAt
) {
    public static PersonResponse from(Person person) {
        if (person == null) {
            return null;
        }
        var addressResponses = person.getAddresses() != null
                ? person.getAddresses().stream().map(AddressResponse::from).toList()
                : List.<AddressResponse>of();

        return new PersonResponse(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getBirthDate(),
                person.getGender(),
                addressResponses,
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }
}
