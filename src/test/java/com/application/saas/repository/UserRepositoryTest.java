package com.application.saas.repository;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve user by username, email, and verify tracking timestamps")
    void shouldSaveAndFindByUsernameAndEmail() {
        User newUser = new User(
                "testuser",
                "test@example.com",
                LocalDate.of(1995, 5, 20),
                "hashedpassword",
                Set.of(Role.ROLE_USER),
                true
        );

        User savedUser = userRepository.save(newUser);
        Optional<User> foundByUsername = userRepository.findByUsername("testuser");
        Optional<User> foundByEmail = userRepository.findByEmail("test@example.com");

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(foundByUsername).isPresent();
        assertThat(foundByUsername.get().getUsername()).isEqualTo("testuser");
        assertThat(foundByUsername.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundByUsername.get().getBirthDate()).isEqualTo(LocalDate.of(1995, 5, 20));
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
    @DisplayName("Should check user existence by username and email")
    void shouldCheckUserExistence() {
        User user = new User(
                "unique_user",
                "unique@example.com",
                LocalDate.of(2000, 1, 1),
                "password",
                Set.of(Role.ROLE_USER),
                true
        );
        userRepository.save(user);

        assertThat(userRepository.existsByUsername("unique_user")).isTrue();
        assertThat(userRepository.existsByUsername("unknown_user")).isFalse();
        assertThat(userRepository.existsByEmail("unique@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }
}
