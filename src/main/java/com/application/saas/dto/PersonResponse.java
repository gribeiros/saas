package com.application.saas.dto;

import com.application.saas.domain.Gender;
import com.application.saas.domain.Person;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PersonResponse(
        UUID id,
        String name,
        String email,
        LocalDate birthDate,
        Gender gender,
        List<AddressResponse> addresses,
        Instant createdAt,
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
