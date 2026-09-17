package com.application.saas.controller;

import com.application.saas.domain.Address;
import com.application.saas.domain.Gender;
import com.application.saas.domain.Person;
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
    @DisplayName("POST /api/login with valid credentials should return 200 OK and JWT token")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("admin", "password123");
        LoginResponse response = new LoginResponse("sample.jwt.token", "Bearer", 3600L);

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("POST /api/login with blank username should return 400 Bad Request with field errors")
    void shouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        LoginRequest request = new LoginRequest("", "password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /api/login with short password should return 400 Bad Request with field errors")
    void shouldReturnBadRequestWhenPasswordIsTooShort() throws Exception {
        LoginRequest request = new LoginRequest("admin", "123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /api/login with invalid credentials should return 401 Unauthorized")
    void shouldReturnUnauthorizedOnBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /api/register with valid payload should return 201 Created with person and addresses")
    void shouldRegisterUserSuccessfully() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "password": "strongPassword123",
                    "name": "Carlos Silva",
                    "email": "carlos@example.com",
                    "birthDate": "1995-06-15",
                    "gender": "MASCULINO",
                    "addresses": [
                        {
                            "street": "Avenida Paulista",
                            "number": "1000",
                            "complement": "Apto 101",
                            "neighborhood": "Bela Vista",
                            "city": "São Paulo",
                            "state": "SP",
                            "zipCode": "01310-100"
                        }
                    ]
                }
                """;

        Address address = Address.builder()
                .id(UUID.randomUUID())
                .street("Avenida Paulista")
                .number("1000")
                .complement("Apto 101")
                .neighborhood("Bela Vista")
                .city("São Paulo")
                .state("SP")
                .zipCode("01310-100")
                .build();

        Person person = Person.builder()
                .id(UUID.randomUUID())
                .name("Carlos Silva")
                .email("carlos@example.com")
                .birthDate(LocalDate.of(1995, 6, 15))
                .gender(Gender.MASCULINO)
                .build();
        person.addAddress(address);

        User registeredUser = User.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .roles(Set.of(Role.ROLE_USER))
                .enabled(true)
                .person(person)
                .build();

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.person.name").value("Carlos Silva"))
                .andExpect(jsonPath("$.person.email").value("carlos@example.com"))
                .andExpect(jsonPath("$.person.birthDate").value("1995-06-15"))
                .andExpect(jsonPath("$.person.gender").value("MASCULINO"))
                .andExpect(jsonPath("$.person.addresses[0].street").value("Avenida Paulista"))
                .andExpect(jsonPath("$.person.addresses[0].city").value("São Paulo"))
                .andExpect(jsonPath("$.person.addresses[0].state").value("SP"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /api/register with invalid email should return 400 Bad Request")
    void shouldReturnBadRequestWhenRegisteringWithInvalidEmail() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "password": "strongPassword123",
                    "name": "Carlos Silva",
                    "email": "not-an-email",
                    "birthDate": "1995-06-15",
                    "gender": "MASCULINO",
                    "addresses": [
                        {
                            "street": "Rua 1",
                            "number": "100",
                            "neighborhood": "Centro",
                            "city": "SP",
                            "state": "SP",
                            "zipCode": "01001-000"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /api/register with empty addresses should return 400 Bad Request")
    void shouldReturnBadRequestWhenAddressesIsEmpty() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "password": "strongPassword123",
                    "name": "Carlos Silva",
                    "email": "carlos@example.com",
                    "birthDate": "1995-06-15",
                    "gender": "MASCULINO",
                    "addresses": []
                }
                """;

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /api/register with more than 2 addresses should return 400 Bad Request")
    void shouldReturnBadRequestWhenMoreThanTwoAddresses() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "password": "strongPassword123",
                    "name": "Carlos Silva",
                    "email": "carlos@example.com",
                    "birthDate": "1995-06-15",
                    "gender": "MASCULINO",
                    "addresses": [
                        {
                            "street": "Rua 1",
                            "number": "100",
                            "neighborhood": "Centro",
                            "city": "SP",
                            "state": "SP",
                            "zipCode": "01001-000"
                        },
                        {
                            "street": "Rua 2",
                            "number": "200",
                            "neighborhood": "Centro",
                            "city": "SP",
                            "state": "SP",
                            "zipCode": "01001-000"
                        },
                        {
                            "street": "Rua 3",
                            "number": "300",
                            "neighborhood": "Centro",
                            "city": "SP",
                            "state": "SP",
                            "zipCode": "01001-000"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details[0].field").value("addresses"))
                .andExpect(jsonPath("$.details[0].message").value("A person can have at most 2 addresses"));
    }

    @Test
    @DisplayName("POST /api/register with invalid CEP should return 400 Bad Request")
    void shouldReturnBadRequestWhenZipCodeIsInvalid() throws Exception {
        String jsonPayload = """
                {
                    "username": "newuser",
                    "password": "strongPassword123",
                    "name": "Carlos Silva",
                    "email": "carlos@example.com",
                    "birthDate": "1995-06-15",
                    "gender": "MASCULINO",
                    "addresses": [
                        {
                            "street": "Rua 1",
                            "number": "100",
                            "neighborhood": "Centro",
                            "city": "SP",
                            "state": "SP",
                            "zipCode": "invalid-cep"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /api/register with duplicate user should return 409 Conflict")
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {
        String jsonPayload = """
                {
                    "username": "duplicateUser",
                    "password": "password123",
                    "name": "Carlos Silva",
                    "email": "dup@example.com",
                    "birthDate": "1990-01-01",
                    "gender": "NAO_INFORMADO",
                    "addresses": [
                        {
                            "street": "Rua 1",
                            "number": "100",
                            "neighborhood": "Centro",
                            "city": "SP",
                            "state": "SP",
                            "zipCode": "01001-000"
                        }
                    ]
                }
                """;

        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Username 'duplicateUser' is already taken"));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Username 'duplicateUser' is already taken"));
    }
}
