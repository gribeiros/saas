package com.application.saas.repository;

import com.application.saas.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return springDataUserRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return springDataUserRepository.findByPersonEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return springDataUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return springDataUserRepository.existsByPersonEmail(email);
    }

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        return springDataUserRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return springDataUserRepository.findById(id);
    }
}
