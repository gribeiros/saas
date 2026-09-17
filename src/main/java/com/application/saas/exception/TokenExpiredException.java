package com.application.saas.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException() {
        super("The provided authentication token has expired");
    }
}

