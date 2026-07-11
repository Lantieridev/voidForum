package com.voidforum.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private String secretKeyBase64;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        secretKeyBase64 = Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKeyBase64);
    }

    @Test
    void generateToken_roundTripsTheUsernameViaExtractUsername() {
        String token = jwtService.generateToken("martin");

        assertThat(jwtService.extractUsername(token)).isEqualTo("martin");
    }

    @Test
    void isTokenValid_trueForAMatchingUsernameAndUnexpiredToken() {
        String token = jwtService.generateToken("martin");

        assertThat(jwtService.isTokenValid(token, "martin")).isTrue();
    }

    @Test
    void isTokenValid_falseWhenTheUsernameDoesNotMatch() {
        String token = jwtService.generateToken("martin");

        assertThat(jwtService.isTokenValid(token, "someone-else")).isFalse();
    }

    @Test
    void isTokenValid_throwsUnguarded_onAnExpiredToken() {
        // Real, known gap: isTokenValid calls extractUsername first, which
        // parses the token unguarded — jjwt's parser rejects an expired
        // token's claims during parsing itself, so this throws instead of
        // returning false. Unlike validateToken (which wraps the same call
        // in try/catch), isTokenValid has no such guard. Its only caller,
        // JwtAuthenticationFilter, now wraps the call in its own try/catch
        // specifically because of this.
        String expiredToken = Jwts.builder()
                .claims(new HashMap<>())
                .subject("martin")
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64)))
                .compact();

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, "martin"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validateToken_trueForAFreshlyGeneratedToken() {
        String token = jwtService.generateToken("martin");

        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_falseForAMalformedToken_insteadOfThrowing() {
        assertThat(jwtService.validateToken("not-a-real-jwt")).isFalse();
    }

    @Test
    void validateToken_falseForATokenSignedWithADifferentKey() {
        SecretKey otherKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String tokenFromAnotherSecret = Jwts.builder()
                .subject("martin")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(otherKey)
                .compact();

        assertThat(jwtService.validateToken(tokenFromAnotherSecret)).isFalse();
    }

    @Test
    void validateToken_falseForAnExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject("martin")
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2))
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64)))
                .compact();

        assertThat(jwtService.validateToken(expiredToken)).isFalse();
    }

    @Test
    void extractUsername_throwsUnguarded_onAMalformedToken() {
        // Documents the real, known gap: unlike validateToken (which catches
        // parse failures), extractUsername parses unguarded. Callers that
        // don't wrap this (JwtAuthenticationFilter does; CommentController's
        // extractUsername helper wraps it explicitly) will propagate the
        // raw parsing exception.
        assertThatThrownBy(() -> jwtService.extractUsername("not-a-real-jwt"))
                .isInstanceOf(RuntimeException.class);
    }
}
