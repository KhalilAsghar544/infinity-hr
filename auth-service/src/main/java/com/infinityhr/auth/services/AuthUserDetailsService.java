package com.infinityhr.auth.services;

import com.infinityhr.auth.repositories.UserRepository;
import com.infinityhr.auth.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username){
        return userRepository.findByUsernameIgnoreCase(username)
                .map(user -> AuthenticatedUser.from(user, Instant.now()))
                .orElseThrow(() -> new UsernameNotFoundException("No User named " + username));
    }
}
