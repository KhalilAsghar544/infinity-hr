package com.infinityhr.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    /** Identifies which key signed a token, so keys can be rotated later without breaking old tokens. */
    public static final String KEY_ID = "inifinityhr-auth-key-1";

    /** Reads the two PEM files once at startup. */
    @Bean
    public KeyPair jwtKeyPair(JwtProperties properties) throws IOException {
        try(InputStream publicIn = properties.publicKey().getInputStream();
            InputStream privateIn = properties.privateKey().getInputStream()) {
            RSAPublicKey publicKey = RsaKeyConverters.x509().convert(publicIn);
            RSAPrivateKey privateKey = RsaKeyConverters.pkcs8().convert(privateIn);
            return new KeyPair(publicKey, privateKey);
        }
    }

    /** Signs tokens with the PRIVATE key. Only auth-service ever has this bean. */
    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) jwtKeyPair.getPublic())
                .privateKey((RSAPrivateKey) jwtKeyPair.getPrivate())
                .keyID(KEY_ID)
                .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeyPair, JwtProperties properties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) jwtKeyPair.getPublic()).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.issuer()));
        return decoder;
    }
}
