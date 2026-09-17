package com.application.saas.service;

import com.application.saas.domain.User;
import com.application.saas.dto.RegisterRequest;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User registerUser(RegisterRequest request);
}
