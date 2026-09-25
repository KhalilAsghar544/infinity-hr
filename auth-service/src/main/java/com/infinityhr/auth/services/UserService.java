package com.infinityhr.auth.services;

import com.infinityhr.auth.entities.Role;
import com.infinityhr.auth.entities.User;
import com.infinityhr.auth.exceptions.DuplicateResourceException;
import com.infinityhr.auth.exceptions.ResourceNotFoundException;
import com.infinityhr.auth.repositories.RoleRepository;
import com.infinityhr.auth.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates an account in PENDING_ACTIVATION state. The raw password is hashed here
     * and never stored or logged anywhere.
     */
    @Transactional
    public User createUser(String username, String email, String rawPassword, Set<String> roleCodes){

        if(userRepository.existsByUsernameIgnoreCase(username)){
            throw new DuplicateResourceException("Username '" + username + "' is already taken");
        }
        if(userRepository.existsByEmailIgnoreCase(email)){
            throw new DuplicateResourceException("Email '" + email + "' is already registered");
        }

        User user = new User(username.trim(), email.trim(), passwordEncoder.encode(rawPassword));

        for (String roleCode : roleCodes){
            Role role = roleRepository.findByCode(roleCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Role '" + roleCode + "' does not exist"));

            user.assignRole(role);
        }

        return userRepository.save(user);
    }

    /**
     * No save() call needed: the user is loaded inside this transaction, so Hibernate
     * notices the change ("dirty checking") and writes it on commit.
     */
    @Transactional
    public void activateUser(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user " + userId + "does not exist"));

        user.activate();
    }
}
