package com.application.saas.service;

import com.application.saas.dto.LoginRequest;
import com.application.saas.dto.LoginResponse;
import com.application.saas.exception.InvalidCredentialsException;
import com.application.saas.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        Objects.requireNonNull(request, "LoginRequest cannot be null");
        log.debug("Authenticating credentials for username: {}", request.username());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(userDetails);
            long expiresInSeconds = jwtService.getExpirationTime() / 1000;

            log.debug("Generated JWT token successfully for user: {}", request.username());
            return LoginResponse.bearer(jwtToken, expiresInSeconds);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed due to bad credentials for user: {}", request.username());
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user {}: {}", request.username(), e.getMessage());
            throw new InvalidCredentialsException("Authentication failed: " + e.getMessage());
        }
    }
}
