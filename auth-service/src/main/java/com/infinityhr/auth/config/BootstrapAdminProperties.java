package com.infinityhr.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "infinityhr.bootstrap.admin")
public record BootstrapAdminProperties(String username, String email, String password) {
}
