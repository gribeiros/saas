package com.application.saas.dto;

import java.util.Set;

public record UserSummaryResponse(
        String username,
        Set<String> roles
) {}

