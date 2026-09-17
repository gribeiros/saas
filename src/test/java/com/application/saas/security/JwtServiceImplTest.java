package com.application.saas.security;

import com.application.saas.exception.InvalidTokenException;
import com.application.saas.exception.TokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceImplTest {

    private static final String SECRET_KEY = "0123456789012345678901234567890123456789";
    private static final long EXPIRATION_MS = 60000;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET_KEY, EXPIRATION_MS);
    }

    @Test
    @DisplayName("Should successfully generate a token and extract username")
    void shouldGenerateTokenAndExtractUsername() {
        UserDetails userDetails = new User("johndoe", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(token).isNotBlank();
        assertThat(extractedUsername).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Should validate token successfully for matching user")
    void shouldValidateTokenForMatchingUser() {
        UserDetails userDetails = new User("johndoe", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false when validating token for non-matching user")
    void shouldReturnFalseForNonMatchingUser() {
        UserDetails user1 = new User("johndoe", "password", Collections.emptyList());
        UserDetails user2 = new User("janedoe", "password", Collections.emptyList());
        String token = jwtService.generateToken(user1);

        boolean isValid = jwtService.isTokenValid(token, user2);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when token has expired")
    void shouldThrowWhenTokenIsExpired() throws InterruptedException {
        JwtServiceImpl shortLivedJwtService = new JwtServiceImpl(SECRET_KEY, 1);
        UserDetails userDetails = new User("johndoe", "password", Collections.emptyList());
        String token = shortLivedJwtService.generateToken(userDetails);

        Thread.sleep(10);

        assertThatThrownBy(() -> shortLivedJwtService.extractUsername(token))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is tampered or malformed")
    void shouldThrowWhenTokenIsMalformed() {
        String malformedToken = "not.a.valid.jwt.token";

        assertThatThrownBy(() -> jwtService.extractUsername(malformedToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Should reject short secret key below 256 bits")
    void shouldRejectShortSecretKey() {
        assertThatThrownBy(() -> new JwtServiceImpl("too-short-secret", EXPIRATION_MS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 256 bits");
    }
}
