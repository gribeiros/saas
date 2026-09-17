package com.application.saas.controller;

import com.application.saas.domain.Address;
import com.application.saas.domain.Gender;
import com.application.saas.domain.Person;
import com.application.saas.domain.Role;
import com.application.saas.domain.User;
import com.application.saas.exception.GlobalExceptionHandler;
import com.application.saas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MeController meController = new MeController(userService);
        UserDetails mockUserDetails = new org.springframework.security.core.userdetails.User(
                "lucas",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return mockUserDetails;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(meController)
                .setCustomArgumentResolvers(authPrincipalResolver)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/me should return 200 OK with full user, person, and address data")
    void shouldReturnFullUserProfile() throws Exception {
        Address address = Address.builder()
                .id(UUID.randomUUID())
                .street("Rua Oscar Freire")
                .number("1420")
                .complement("Apto 82")
                .neighborhood("Cerqueira César")
                .city("São Paulo")
                .state("SP")
                .zipCode("01426-001")
                .build();

        Person person = Person.builder()
                .id(UUID.randomUUID())
                .name("Lucas Mendonça")
                .email("lucas@gmail.com")
                .birthDate(LocalDate.of(1993, 4, 18))
                .gender(Gender.MASCULINO)
                .build();
        person.addAddress(address);

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("lucas")
                .password("hash")
                .roles(Set.of(Role.ROLE_USER))
                .enabled(true)
                .person(person)
                .build();

        when(userService.findByUsername("lucas")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("lucas"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.person.name").value("Lucas Mendonça"))
                .andExpect(jsonPath("$.person.email").value("lucas@gmail.com"))
                .andExpect(jsonPath("$.person.birthDate").value("1993-04-18"))
                .andExpect(jsonPath("$.person.gender").value("MASCULINO"))
                .andExpect(jsonPath("$.person.addresses[0].street").value("Rua Oscar Freire"))
                .andExpect(jsonPath("$.person.addresses[0].city").value("São Paulo"))
                .andExpect(jsonPath("$.person.addresses[0].state").value("SP"))
                .andExpect(jsonPath("$.person.addresses[0].zipCode").value("01426-001"));
    }

    @Test
    @DisplayName("GET /api/me when user not found should return 404 Not Found")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.findByUsername("lucas")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}

