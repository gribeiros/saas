package com.application.saas.controller;

import com.application.saas.domain.User;
import com.application.saas.dto.ErrorResponse;
import com.application.saas.dto.PersonResponse;
import com.application.saas.dto.UserSummaryResponse;
import com.application.saas.exception.UserNotFoundException;
import com.application.saas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Profile", description = "Endpoints for managing authenticated user profile and addresses")
public class MeController {

    private final UserService userService;

    @Operation(
            summary = "Get authenticated user profile",
            description = "Retrieves profile information, personal data, and associated addresses of the currently authenticated user.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSummaryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid, or expired JWT bearer token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found in system",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserSummaryResponse> getAuthenticatedUserProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.debug("Fetching profile for authenticated user: {}", userDetails.getUsername());
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));

        return ResponseEntity.ok(toUserSummaryResponse(user));
    }

    private UserSummaryResponse toUserSummaryResponse(User user) {
        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                roles,
                PersonResponse.from(user.getPerson()),
                user.getCreatedAt()
        );
    }
}
