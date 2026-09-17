package com.application.saas.domain;

import com.application.saas.exception.AddressLimitExceededException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonTest {

    private Address createAddress(String street) {
        return Address.builder()
                .id(UUID.randomUUID())
                .street(street)
                .number("100")
                .neighborhood("Centro")
                .city("São Paulo")
                .state("SP")
                .zipCode("01001-000")
                .build();
    }

    @Test
    @DisplayName("Should allow adding up to 2 addresses to a person")
    void shouldAllowAddingUpToTwoAddresses() {
        Person person = Person.builder()
                .id(UUID.randomUUID())
                .name("Carlos Silva")
                .email("carlos@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MASCULINO)
                .build();

        person.addAddress(createAddress("Rua A"));
        person.addAddress(createAddress("Rua B"));

        assertThat(person.getAddresses()).hasSize(2);
        assertThat(person.getAddresses().get(0).getPerson()).isEqualTo(person);
        assertThat(person.getAddresses().get(1).getPerson()).isEqualTo(person);
    }

    @Test
    @DisplayName("Should throw AddressLimitExceededException when trying to add a third address")
    void shouldThrowWhenAddingThirdAddress() {
        Person person = Person.builder()
                .id(UUID.randomUUID())
                .name("Carlos Silva")
                .email("carlos@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MASCULINO)
                .build();

        person.addAddress(createAddress("Rua A"));
        person.addAddress(createAddress("Rua B"));

        Address thirdAddress = createAddress("Rua C");

        assertThatThrownBy(() -> person.addAddress(thirdAddress))
                .isInstanceOf(AddressLimitExceededException.class)
                .hasMessage("A person cannot have more than 2 addresses");
    }
}

