package com.application.saas.service;

import com.application.saas.domain.Address;
import com.application.saas.domain.Person;
import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import com.application.saas.dto.AddressRequest;
import com.application.saas.dto.RegisterRequest;
import com.application.saas.exception.UserAlreadyExistsException;
import com.application.saas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Attempting to register user with username: {} and email: {}", request.username(), request.email());

        if (existsByUsername(request.username())) {
            log.warn("Registration rejected: username '{}' already taken", request.username());
            throw new UserAlreadyExistsException("Username '" + request.username() + "' is already taken");
        }

        if (existsByEmail(request.email())) {
            log.warn("Registration rejected: email '{}' already registered", request.email());
            throw new UserAlreadyExistsException("Email '" + request.email() + "' is already registered");
        }

        Person person = Person.builder()
                .name(request.name())
                .email(request.email())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .build();

        if (request.addresses() != null) {
            for (AddressRequest addrReq : request.addresses()) {
                Address address = Address.builder()
                        .street(addrReq.street())
                        .number(addrReq.number())
                        .complement(addrReq.complement())
                        .neighborhood(addrReq.neighborhood())
                        .city(addrReq.city())
                        .state(addrReq.state().toUpperCase())
                        .zipCode(addrReq.zipCode())
                        .build();
                person.addAddress(address);
            }
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.builder()
                .username(request.username())
                .password(encodedPassword)
                .roles(Set.of(Role.ROLE_USER))
                .enabled(true)
                .build();
        user.setPerson(person);

        User saved = userRepository.save(user);
        log.info("User successfully registered with id: {} and username: {}", saved.getId(), saved.getUsername());
        return saved;
    }
}
