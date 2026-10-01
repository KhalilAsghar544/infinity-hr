package com.infinityhr.auth.services;

import com.infinityhr.auth.config.JwtProperties;
import com.infinityhr.auth.dto.TokenResponse;
import com.infinityhr.auth.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public TokenResponse issueAccessToken(AuthenticatedUser user){

        Instant now = Instant.now();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())                         // iss: who created the token
                .subject(user.getId().toString())                       // sub: who the token is about
                .issuedAt(now)                                          // iat
                .expiresAt(now.plus(jwtProperties.accessTokenTtl()))    // exp: rejected after this
                .id(UUID.randomUUID().toString())                       // jti: unique id, used for logout later
                .claim("username", user.getUsername())
                .claim("permissions", List.copyOf(user.getPermissions()));

        if(user.getEmployeeId() != null){
            claims.claim("employeeId", user.getEmployeeId().toString());
        }
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();

        return new TokenResponse(token, "Bearer", jwtProperties.accessTokenTtl().toSeconds());
    }
}
