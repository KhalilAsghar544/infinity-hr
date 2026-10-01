package com.infinityhr.auth.controllers;

import com.infinityhr.auth.dto.CurentUserResponse;
import com.infinityhr.auth.dto.LoginRequest;
import com.infinityhr.auth.dto.TokenResponse;
import com.infinityhr.auth.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    /**
     * Reads everything from the verified token; no database call. This is how every other
     * service will know who is calling.
     */

    @GetMapping("/me")
    public CurentUserResponse me(@AuthenticationPrincipal Jwt jwt){
        return new CurentUserResponse(
                jwt.getSubject(),
                jwt.getClaimAsString("username"),
                jwt.getClaimAsString("employeeId"),
                jwt.getClaimAsStringList("permisions"),
                jwt.getExpiresAt()
        );
    }
}
