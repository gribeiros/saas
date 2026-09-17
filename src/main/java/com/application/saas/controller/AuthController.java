package com.application.saas.controller;

import com.application.saas.domain.User;
import com.application.saas.dto.LoginRequest;
import com.application.saas.dto.LoginResponse;
import com.application.saas.dto.RegisterRequest;
import com.application.saas.dto.RegisterResponse;
import com.application.saas.service.AuthenticationService;
import com.application.saas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login attempt for user: {}", request.username());
        LoginResponse response = authenticationService.login(request);
        log.info("Authentication successful for user: {}", request.username());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for username: {} and email: {}", request.username(), request.email());
        User user = userService.registerUser(request);
        var roleNames = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        var response = new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBirthDate(),
                roleNames,
                user.getCreatedAt()
        );
        log.info("Registration completed successfully for user id: {}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
