package com.infinityhr.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

@ConfigurationProperties(prefix = "infinityhr.jwt")
public record JwtProperties(String issuer, Duration accessTokenTtl, Resource privateKey, Resource publicKey) {

}
