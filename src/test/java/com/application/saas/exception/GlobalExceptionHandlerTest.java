package com.application.saas.exception;

import com.application.saas.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/test/path");
    }

    @Test
    @DisplayName("Should handle InvalidCredentialsException with 401 Unauthorized")
    void shouldHandleInvalidCredentials() {
        var ex = new InvalidCredentialsException("Custom credentials error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCredentials(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Custom credentials error");
        assertThat(response.getBody().path()).isEqualTo("/test/path");
    }

    @Test
    @DisplayName("Should handle BadCredentialsException with 401 Unauthorized")
    void shouldHandleBadCredentials() {
        var ex = new BadCredentialsException("Bad credentials");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentials(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("Should handle UserNotFoundException with 404 Not Found")
    void shouldHandleUserNotFound() {
        var ex = new UserNotFoundException("nonexistent_user");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).contains("nonexistent_user");
    }

    @Test
    @DisplayName("Should handle UserAlreadyExistsException with 409 Conflict")
    void shouldHandleUserAlreadyExists() {
        var ex = new UserAlreadyExistsException("User already exists: duplicate_user");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserAlreadyExists(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().error()).isEqualTo("Conflict");
        assertThat(response.getBody().message()).contains("duplicate_user");
    }

    @Test
    @DisplayName("Should handle TokenExpiredException with 401 Unauthorized")
    void shouldHandleTokenExpired() {
        var ex = new TokenExpiredException("Token expired");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTokenExpired(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Token expired");
    }

    @Test
    @DisplayName("Should handle InvalidTokenException with 401 Unauthorized")
    void shouldHandleInvalidToken() {
        var ex = new InvalidTokenException("Malformed token");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidToken(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Malformed token");
    }

    @Test
    @DisplayName("Should handle AccessDeniedException with 403 Forbidden")
    void shouldHandleAccessDenied() {
        var ex = new AccessDeniedException("Access denied");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDenied(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should handle AddressLimitExceededException with 400 Bad Request")
    void shouldHandleAddressLimitExceeded() {
        var ex = new AddressLimitExceededException("A person can have at most 2 addresses");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAddressLimitExceeded(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("A person can have at most 2 addresses");
        assertThat(response.getBody().path()).isEqualTo("/test/path");
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 Internal Server Error")
    void shouldHandleGenericException() {
        var ex = new RuntimeException("Unexpected boom");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
    }
}

