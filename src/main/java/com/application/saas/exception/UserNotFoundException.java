package com.application.saas.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }

    public UserNotFoundException(UUID id) {
        super("User not found: " + id);
    }
}

