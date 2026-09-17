package com.application.saas.service;

import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import com.application.saas.dto.RegisterRequest;
import com.application.saas.exception.UserAlreadyExistsException;
import com.application.saas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should register new user successfully with encoded password via RegisterRequest")
    void shouldRegisterNewUserViaRequest() {
        var request = new RegisterRequest(
                "john@example.com",
                LocalDate.of(1992, 8, 15),
                "johndoe",
                "password123"
        );

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("bcrypt-encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(request);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("johndoe");
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
        assertThat(savedUser.getBirthDate()).isEqualTo(LocalDate.of(1992, 8, 15));
        assertThat(savedUser.getPassword()).isEqualTo("bcrypt-encoded-password");
        assertThat(savedUser.getRoles()).containsExactly(Role.ROLE_USER);
        assertThat(savedUser.isEnabled()).isTrue();

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering user with duplicate username")
    void shouldThrowWhenRegisteringExistingUsername() {
        var request = new RegisterRequest(
                "john@example.com",
                LocalDate.of(1992, 8, 15),
                "existingUser",
                "password123"
        );

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("existingUser");
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering user with duplicate email")
    void shouldThrowWhenRegisteringExistingEmail() {
        var request = new RegisterRequest(
                "existing@example.com",
                LocalDate.of(1992, 8, 15),
                "uniqueUser",
                "password123"
        );

        when(userRepository.existsByUsername("uniqueUser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("existing@example.com");
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        User user = new User(
                "admin",
                "admin@example.com",
                LocalDate.of(1985, 1, 1),
                "hash",
                Set.of(Role.ROLE_ADMIN),
                true
        );
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("admin");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
        assertThat(result.get().getEmail()).isEqualTo("admin@example.com");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        User user = new User(
                "admin",
                "admin@example.com",
                LocalDate.of(1985, 1, 1),
                "hash",
                Set.of(Role.ROLE_ADMIN),
                true
        );
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("admin@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should return empty optional when finding user with blank or null username/email")
    void shouldReturnEmptyForNullInputs() {
        assertThat(userService.findByUsername(null)).isEmpty();
        assertThat(userService.findByUsername("  ")).isEmpty();
        assertThat(userService.findByEmail(null)).isEmpty();
        assertThat(userService.findByEmail("  ")).isEmpty();
    }
}
