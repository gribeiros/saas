package com.application.saas.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String generateToken(UserDetails userDetails);

    String generateToken(String username, Map<String, Object> extraClaims);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    long getExpirationTime();
}
