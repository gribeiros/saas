package com.application.saas.exception;

public class AddressLimitExceededException extends RuntimeException {

    public AddressLimitExceededException(String message) {
        super(message);
    }

    public AddressLimitExceededException(int maxAllowed) {
        super("A person can have at most " + maxAllowed + " addresses");
    }
}

