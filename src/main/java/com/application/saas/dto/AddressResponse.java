package com.application.saas.dto;

import com.application.saas.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Address response data")
public record AddressResponse(
        @Schema(description = "Address unique identifier", example = "5fa23d11-b1e9-4e78-9840-0255a5dc6011")
        UUID id,

        @Schema(description = "Street name", example = "Rua Oscar Freire")
        String street,

        @Schema(description = "Number", example = "1420")
        String number,

        @Schema(description = "Complement", example = "Apto 82 - Bloco B")
        String complement,

        @Schema(description = "Neighborhood", example = "Cerqueira César")
        String neighborhood,

        @Schema(description = "City", example = "São Paulo")
        String city,

        @Schema(description = "State abbreviation (UF)", example = "SP")
        String state,

        @Schema(description = "Zip code (CEP)", example = "01426-001")
        String zipCode,

        @Schema(description = "Timestamp when the address was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the address was last updated")
        Instant updatedAt
) {
    public static AddressResponse from(Address addr) {
        if (addr == null) {
            return null;
        }
        return new AddressResponse(
                addr.getId(),
                addr.getStreet(),
                addr.getNumber(),
                addr.getComplement(),
                addr.getNeighborhood(),
                addr.getCity(),
                addr.getState(),
                addr.getZipCode(),
                addr.getCreatedAt(),
                addr.getUpdatedAt()
        );
    }
}
