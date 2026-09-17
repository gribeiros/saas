package com.application.saas.service;

import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import com.application.saas.dto.RegisterRequest;
import com.application.saas.exception.UserAlreadyExistsException;
import com.application.saas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        log.debug("Looking up user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        log.debug("Looking up user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User registerUser(RegisterRequest request) {
        Objects.requireNonNull(request, "RegisterRequest cannot be null");
        return registerUser(
                request.username(),
                request.email(),
                request.birthDate(),
                request.password(),
                Set.of(Role.ROLE_USER)
        );
    }

    @Override
    @Transactional
    public User registerUser(String username, String email, LocalDate birthDate, String rawPassword, Set<Role> roles) {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(birthDate, "Birth date cannot be null");
        Objects.requireNonNull(rawPassword, "Password cannot be null");

        log.info("Attempting to register user with username: {} and email: {}", username, email);

        if (existsByUsername(username)) {
            log.warn("Registration rejected: username '{}' already taken", username);
            throw new UserAlreadyExistsException("Username '" + username + "' is already taken");
        }

        if (existsByEmail(email)) {
            log.warn("Registration rejected: email '{}' already registered", email);
            throw new UserAlreadyExistsException("Email '" + email + "' is already registered");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.builder()
                .username(username)
                .email(email)
                .birthDate(birthDate)
                .password(encodedPassword)
                .roles(roles != null && !roles.isEmpty() ? roles : Set.of(Role.ROLE_USER))
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User successfully registered with id: {} and username: {}", saved.getId(), username);
        return saved;
    }
}
