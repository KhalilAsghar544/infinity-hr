package com.infinityhr.auth.config;


import com.infinityhr.auth.entities.User;
import com.infinityhr.auth.repositories.UserRepository;
import com.infinityhr.auth.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Creates the first SUPER_ADMIN when the service starts, so there is someone who can log in
 * and create everyone else. Safe to run on every startup: it does nothing if the admin exists.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final BootstrapAdminProperties properties;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args){
        if(!StringUtils.hasText(properties.password())){
            log.warn("BOOTSTRAP_ADMIN_PASSWORD is not set; skipping super admin creation");
            return;
        }
        if(userRepository.existsByUsernameIgnoreCase(properties.username())){
            log.info("Super admin '{}' already exist", properties.username());
            return;
        }

        User admin = userService.createUser(properties.username(), properties.email(), properties.password(), Set.of("SUPER_ADMIN"));
        userService.activateUser(admin.getId());
        log.info("Created super admin '{}'", admin.getUsername());
    }
}
