package com.application.saas.repository;

import com.application.saas.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataUserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"roles", "person", "person.addresses"})
    Optional<User> findByUsername(String username);

    Optional<User> findByPersonEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPersonEmail(String email);
}
