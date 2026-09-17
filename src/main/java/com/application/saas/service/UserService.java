package com.application.saas.service;

import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import com.application.saas.dto.RegisterRequest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User registerUser(RegisterRequest request);

    User registerUser(String username, String email, LocalDate birthDate, String rawPassword, Set<Role> roles);
}
