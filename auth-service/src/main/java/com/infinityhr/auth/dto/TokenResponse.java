package com.infinityhr.auth.dto;

/**
 * @param expiresIn seconds until the access token expires, so the client knows when to get a new one
 */
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
