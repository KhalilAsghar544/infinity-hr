package com.infinityhr.auth.services;

import com.infinityhr.auth.dto.LoginRequest;
import com.infinityhr.auth.dto.TokenResponse;
import com.infinityhr.auth.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * Throws BadCredentialsException, DisabledException or LockedException when the login is refused;
     * GlobalExceptionHandler turns those into 401 responses.
     */
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        assert user != null;
        return tokenService.issueAccessToken(user);
    }
}
