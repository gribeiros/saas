package com.application.saas.controller;

import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import com.application.saas.dto.LoginRequest;
import com.application.saas.dto.LoginResponse;
import com.application.saas.dto.RegisterRequest;
import com.application.saas.exception.GlobalExceptionHandler;
import com.application.saas.exception.InvalidCredentialsException;
import com.application.saas.exception.UserAlreadyExistsException;
import com.application.saas.service.AuthenticationService;
import com.application.saas.service.UserService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(authenticationService, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /login with valid credentials should return 200 OK and JWT token")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("admin", "password123");
        LoginResponse response = new LoginResponse("sample.jwt.token", "Bearer", 86400L);

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400));
    }

    @Test
    @DisplayName("POST /login with blank username should return 400 Bad Request with field errors")
    void shouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        LoginRequest request = new LoginRequest("", "password123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /login with short password should return 400 Bad Request with field errors")
    void shouldReturnBadRequestWhenPasswordIsTooShort() throws Exception {
        LoginRequest request = new LoginRequest("admin", "123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /login with invalid credentials should return 401 Unauthorized")
    void shouldReturnUnauthorizedOnBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /register with valid payload should return 201 Created and user details")
    void shouldRegisterUserSuccessfully() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "birthDate": "1995-06-15",
                    "password": "strongPassword123"
                }
                """;

        User registeredUser = User.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .email("newuser@example.com")
                .birthDate(LocalDate.of(1995, 6, 15))
                .roles(Set.of(Role.ROLE_USER))
                .enabled(true)
                .build();

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.birthDate").value("1995-06-15"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /register with invalid email should return 400 Bad Request")
    void shouldReturnBadRequestWhenRegisteringWithInvalidEmail() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "email": "not-an-email",
                    "birthDate": "1995-06-15",
                    "password": "strongPassword123"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /register with future birth date should return 400 Bad Request")
    void shouldReturnBadRequestWhenBirthDateIsInFuture() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "birthDate": "2099-01-01",
                    "password": "strongPassword123"
                }
                """;

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /register with duplicate user should return 409 Conflict")
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {
        String jsonPayload = """
                {
                    "username": "duplicateUser",
                    "email": "dup@example.com",
                    "birthDate": "1990-01-01",
                    "password": "password123"
                }
                """;

        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Username 'duplicateUser' is already taken"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Username 'duplicateUser' is already taken"));
    }
}
