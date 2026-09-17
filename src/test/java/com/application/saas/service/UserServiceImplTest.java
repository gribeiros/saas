package com.application.saas.service;

import com.application.saas.domain.Gender;
import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import com.application.saas.dto.AddressRequest;
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
import java.util.List;
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
    @DisplayName("Should register new user successfully with person and multiple addresses")
    void shouldRegisterNewUserViaRequest() {
        var addr1 = new AddressRequest(
                "Avenida Paulista",
                "1000",
                "Apto 101",
                "Bela Vista",
                "São Paulo",
                "SP",
                "01310-100"
        );
        var addr2 = new AddressRequest(
                "Rua XV de Novembro",
                "200",
                null,
                "Centro",
                "Curitiba",
                "PR",
                "80020-310"
        );

        var request = new RegisterRequest(
                "johndoe",
                "password123",
                "John Doe",
                "john@example.com",
                LocalDate.of(1992, 8, 15),
                Gender.MASCULINO,
                List.of(addr1, addr2)
        );

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("bcrypt-encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(request);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("johndoe");
        assertThat(savedUser.getPassword()).isEqualTo("bcrypt-encoded-password");
        assertThat(savedUser.getRoles()).containsExactly(Role.ROLE_USER);
        assertThat(savedUser.isEnabled()).isTrue();

        assertThat(savedUser.getPerson()).isNotNull();
        assertThat(savedUser.getPerson().getName()).isEqualTo("John Doe");
        assertThat(savedUser.getPerson().getEmail()).isEqualTo("john@example.com");
        assertThat(savedUser.getPerson().getBirthDate()).isEqualTo(LocalDate.of(1992, 8, 15));
        assertThat(savedUser.getPerson().getGender()).isEqualTo(Gender.MASCULINO);
        assertThat(savedUser.getPerson().getAddresses()).hasSize(2);
        assertThat(savedUser.getPerson().getAddresses().get(0).getCity()).isEqualTo("São Paulo");
        assertThat(savedUser.getPerson().getAddresses().get(1).getCity()).isEqualTo("Curitiba");

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering user with duplicate username")
    void shouldThrowWhenRegisteringExistingUsername() {
        var addr = new AddressRequest("Rua 1", "10", null, "Bairro", "Cidade", "SP", "01001-000");
        var request = new RegisterRequest(
                "existingUser",
                "password123",
                "John",
                "john@example.com",
                LocalDate.of(1992, 8, 15),
                Gender.MASCULINO,
                List.of(addr)
        );

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("existingUser");
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when registering user with duplicate email")
    void shouldThrowWhenRegisteringExistingEmail() {
        var addr = new AddressRequest("Rua 1", "10", null, "Bairro", "Cidade", "SP", "01001-000");
        var request = new RegisterRequest(
                "uniqueUser",
                "password123",
                "John",
                "existing@example.com",
                LocalDate.of(1992, 8, 15),
                Gender.MASCULINO,
                List.of(addr)
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
        User user = new User("admin", "hash", Set.of(Role.ROLE_ADMIN), true);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("admin");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        User user = new User("admin", "hash", Set.of(Role.ROLE_ADMIN), true);
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
