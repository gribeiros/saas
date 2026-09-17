package com.application.saas.repository;

import com.application.saas.domain.Address;
import com.application.saas.domain.Gender;
import com.application.saas.domain.Person;
import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve user with person and multiple addresses, verifying audit timestamps")
    void shouldSaveAndFindByUsernameAndEmail() {
        Address address1 = Address.builder()
                .street("Avenida Paulista")
                .number("1000")
                .complement("Apto 101")
                .neighborhood("Bela Vista")
                .city("São Paulo")
                .state("SP")
                .zipCode("01310-100")
                .build();

        Address address2 = Address.builder()
                .street("Rua XV de Novembro")
                .number("200")
                .neighborhood("Centro")
                .city("Curitiba")
                .state("PR")
                .zipCode("80020-310")
                .build();

        Person person = Person.builder()
                .name("Maria Silva")
                .email("maria@example.com")
                .birthDate(LocalDate.of(1995, 5, 20))
                .gender(Gender.FEMININO)
                .build();
        person.addAddress(address1);
        person.addAddress(address2);

        User newUser = new User("maria", "hashedpassword", Set.of(Role.ROLE_USER), true);
        newUser.setPerson(person);

        User savedUser = userRepository.save(newUser);
        Optional<User> foundByUsername = userRepository.findByUsername("maria");
        Optional<User> foundByEmail = userRepository.findByEmail("maria@example.com");

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getPerson()).isNotNull();
        assertThat(savedUser.getPerson().getId()).isNotNull();
        assertThat(savedUser.getPerson().getCreatedAt()).isNotNull();
        assertThat(savedUser.getPerson().getUpdatedAt()).isNotNull();
        assertThat(savedUser.getPerson().getAddresses()).hasSize(2);

        assertThat(foundByUsername).isPresent();
        assertThat(foundByUsername.get().getUsername()).isEqualTo("maria");
        assertThat(foundByUsername.get().getPerson().getName()).isEqualTo("Maria Silva");
        assertThat(foundByUsername.get().getPerson().getGender()).isEqualTo(Gender.FEMININO);

        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Should return empty when user does not exist")
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> foundUsername = userRepository.findByUsername("nonexistent");
        Optional<User> foundEmail = userRepository.findByEmail("nonexistent@example.com");

        assertThat(foundUsername).isEmpty();
        assertThat(foundEmail).isEmpty();
    }

    @Test
    @DisplayName("Should check user existence by username and person email")
    void shouldCheckUserExistence() {
        Person person = Person.builder()
                .name("Carlos Oliveira")
                .email("carlos@example.com")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(Gender.MASCULINO)
                .build();

        User user = new User("carlos", "password", Set.of(Role.ROLE_USER), true);
        user.setPerson(person);
        userRepository.save(user);

        assertThat(userRepository.existsByUsername("carlos")).isTrue();
        assertThat(userRepository.existsByUsername("unknown_user")).isFalse();
        assertThat(userRepository.existsByEmail("carlos@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should return empty or false when lookup parameters are null or blank")
    void shouldHandleNullAndBlankLookups() {
        assertThat(userRepository.findByUsername(null)).isEmpty();
        assertThat(userRepository.findByUsername("   ")).isEmpty();
        assertThat(userRepository.findByEmail(null)).isEmpty();
        assertThat(userRepository.findByEmail("   ")).isEmpty();
        assertThat(userRepository.existsByUsername(null)).isFalse();
        assertThat(userRepository.existsByUsername("   ")).isFalse();
        assertThat(userRepository.existsByEmail(null)).isFalse();
        assertThat(userRepository.existsByEmail("   ")).isFalse();
        assertThat(userRepository.findById(null)).isEmpty();
    }

    @Test
    @DisplayName("Should find user by id when exists")
    void shouldFindByIdWhenExists() {
        Person person = Person.builder()
                .name("Ana Costa")
                .email("ana@example.com")
                .birthDate(LocalDate.of(1998, 3, 10))
                .gender(Gender.FEMININO)
                .build();

        User user = new User("anacosta", "secret", Set.of(Role.ROLE_USER), true);
        user.setPerson(person);
        User saved = userRepository.save(user);

        assertThat(userRepository.findById(saved.getId())).isPresent();
        assertThat(userRepository.findById(UUID.randomUUID())).isEmpty();
    }
}
