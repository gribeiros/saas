package com.application.saas.controller;

import com.application.saas.dto.UserSummaryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getAuthenticatedUserProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.debug("Fetching profile for authenticated user: {}", userDetails.getUsername());
        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        var response = new UserSummaryResponse(userDetails.getUsername(), roles);
        return ResponseEntity.ok(response);
    }
}
