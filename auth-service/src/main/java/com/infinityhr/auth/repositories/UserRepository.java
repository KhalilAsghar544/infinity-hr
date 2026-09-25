package com.infinityhr.auth.repositories;

import com.infinityhr.auth.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /** Loads roles and their permissions in the same query, ready to build a token. */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);
}
