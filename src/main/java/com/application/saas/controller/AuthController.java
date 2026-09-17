package com.application.saas.controller;

import com.application.saas.domain.User;
import com.application.saas.dto.ErrorResponse;
import com.application.saas.dto.LoginRequest;
import com.application.saas.dto.LoginResponse;
import com.application.saas.dto.PersonResponse;
import com.application.saas.dto.RegisterRequest;
import com.application.saas.dto.RegisterResponse;
import com.application.saas.service.AuthenticationService;
import com.application.saas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Authentication & Registration", description = "Endpoints for user login and account registration")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and returns a signed JWT bearer token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error in request payload",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login attempt for user: {}", request.username());
        LoginResponse response = authenticationService.login(request);
        log.info("Authentication successful for user: {}", request.username());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with personal details and multiple Brazilian addresses."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error in request payload",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already registered",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for username: {} and email: {}", request.username(), request.email());
        User user = userService.registerUser(request);
        var roleNames = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        var response = new RegisterResponse(
                user.getId(),
                user.getUsername(),
                roleNames,
                PersonResponse.from(user.getPerson()),
                user.getCreatedAt()
        );
        log.info("Registration completed successfully for user id: {}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
