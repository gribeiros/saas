package com.application.saas.dto;

import com.application.saas.domain.Address;

import java.time.Instant;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        Instant createdAt,
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
