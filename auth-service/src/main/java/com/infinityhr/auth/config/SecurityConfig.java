package com.infinityhr.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * Hashes passwords with BCrypt and prefixes the hash with the algorithm, e.g. "{bcrypt}$2a$10$...".
     * The prefix lets us switch to a stronger algorithm later while old hashes keep working.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
