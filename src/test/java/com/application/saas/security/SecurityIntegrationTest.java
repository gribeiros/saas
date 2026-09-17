package com.application.saas.security;

import com.application.saas.domain.Gender;
import com.application.saas.dto.AddressRequest;
import com.application.saas.dto.LoginRequest;
import com.application.saas.dto.LoginResponse;
import com.application.saas.dto.RegisterRequest;
import com.application.saas.service.UserService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        if (!userService.existsByUsername("admin")) {
            var addr = new AddressRequest(
                    "Rua Principal",
                    "100",
                    "Bloco A",
                    "Centro",
                    "Brasilia",
                    "DF",
                    "70000-000"
            );
            var req = new RegisterRequest(
                    "admin",
                    "password123",
                    "Administrador",
                    "admin@example.com",
                    LocalDate.of(1990, 1, 1),
                    Gender.NAO_INFORMADO,
                    List.of(addr)
            );
            userService.registerUser(req);
        }
    }

    @Test
    @DisplayName("POST /api/register should be publicly accessible and register a new user with person and addresses")
    void shouldRegisterNewUserPublicly() throws Exception {
        String jsonPayload = """
                {
                    "username": "integration_user",
                    "password": "strongPassword123",
                    "name": "Usuario Integracao",
                    "email": "integration@example.com",
                    "birthDate": "1994-03-25",
                    "gender": "MASCULINO",
                    "addresses": [
                        {
                            "street": "Avenida Brasil",
                            "number": "500",
                            "complement": "Sala 12",
                            "neighborhood": "Funcionarios",
                            "city": "Belo Horizonte",
                            "state": "MG",
                            "zipCode": "30140-000"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.person.name").value("Usuario Integracao"))
                .andExpect(jsonPath("$.person.email").value("integration@example.com"))
                .andExpect(jsonPath("$.person.birthDate").value("1994-03-25"))
                .andExpect(jsonPath("$.person.gender").value("MASCULINO"))
                .andExpect(jsonPath("$.person.addresses[0].city").value("Belo Horizonte"));

        LoginRequest loginRequest = new LoginRequest("integration_user", "strongPassword123");
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                LoginResponse.class
        );

        mockMvc.perform(get("/api/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.person.name").value("Usuario Integracao"))
                .andExpect(jsonPath("$.person.email").value("integration@example.com"))
                .andExpect(jsonPath("$.person.addresses[0].city").value("Belo Horizonte"));
    }

    @Test
    @DisplayName("POST /api/register with duplicate email should return 409 Conflict")
    void shouldRejectDuplicateEmailOnRegister() throws Exception {
        String jsonPayload = """
                {
                    "username": "different_username",
                    "password": "password123",
                    "name": "Outro Nome",
                    "email": "admin@example.com",
                    "birthDate": "1992-05-10",
                    "gender": "FEMININO",
                    "addresses": [
                        {
                            "street": "Rua 2",
                            "number": "20",
                            "neighborhood": "Centro",
                            "city": "Rio de Janeiro",
                            "state": "RJ",
                            "zipCode": "20000-000"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    @DisplayName("POST /api/register with more than 2 addresses should return 400 Bad Request")
    void shouldRejectRegisterWithMoreThanTwoAddresses() throws Exception {
        String jsonPayload = """
                {
                    "username": "three_addresses_user",
                    "password": "password123",
                    "name": "Tres Enderecos",
                    "email": "three@example.com",
                    "birthDate": "1992-05-10",
                    "gender": "FEMININO",
                    "addresses": [
                        {
                            "street": "Rua 1",
                            "number": "10",
                            "neighborhood": "Centro",
                            "city": "Sao Paulo",
                            "state": "SP",
                            "zipCode": "01001-000"
                        },
                        {
                            "street": "Rua 2",
                            "number": "20",
                            "neighborhood": "Centro",
                            "city": "Rio de Janeiro",
                            "state": "RJ",
                            "zipCode": "20000-000"
                        },
                        {
                            "street": "Rua 3",
                            "number": "30",
                            "neighborhood": "Centro",
                            "city": "Belo Horizonte",
                            "state": "MG",
                            "zipCode": "30140-000"
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
    @DisplayName("POST /api/login with valid admin credentials should return 200 and valid JWT")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("admin", "password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    @DisplayName("POST /api/login with invalid credentials should return 401 Unauthorized")
    void shouldFailLoginWithBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrong-password");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("GET /api/me without token should be blocked with 401 Unauthorized")
    void shouldBlockUnauthenticatedRequestToProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/me with valid Bearer token should return 200 OK and user summary")
    void shouldAllowAuthenticatedRequestWithValidJwt() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                LoginResponse.class
        );

        mockMvc.perform(get("/api/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    @DisplayName("GET /api/me with forged/invalid token should return 401 Unauthorized")
    void shouldRejectForgedJwt() throws Exception {
        mockMvc.perform(get("/api/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.forged.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Any new arbitrary route without token should be blocked with 401 Unauthorized")
    void shouldBlockAnyNewRouteByDefault() throws Exception {
        mockMvc.perform(get("/api/new-feature/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /v3/api-docs should be publicly accessible and return 200 OK with OpenAPI schema")
    void shouldAllowPublicAccessToSwaggerApiDocs() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("SaaS Application API"))
                .andExpect(jsonPath("$.paths['/api/login']").exists())
                .andExpect(jsonPath("$.paths['/api/register']").exists())
                .andExpect(jsonPath("$.paths['/api/me']").exists())
                .andExpect(jsonPath("$.components.securitySchemes.BearerAuth").exists());
    }

    @Test
    @DisplayName("GET /swagger-ui/index.html should be publicly accessible without authentication")
    void shouldAllowPublicAccessToSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
