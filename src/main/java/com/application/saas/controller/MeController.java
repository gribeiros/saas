package com.application.saas.controller;

import com.application.saas.domain.User;
import com.application.saas.dto.PersonResponse;
import com.application.saas.dto.UserSummaryResponse;
import com.application.saas.exception.UserNotFoundException;
import com.application.saas.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getAuthenticatedUserProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.debug("Fetching profile for authenticated user: {}", userDetails.getUsername());
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));

        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        var response = new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                roles,
                PersonResponse.from(user.getPerson()),
                user.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}
