package com.tinysand.system.errors;

public class RegistrationError extends RuntimeException {
    public RegistrationError(String message) {
        super(message);
    }

    public RegistrationError(Throwable cause) {
        super(cause);
    }

    public RegistrationError(String message,
                             Throwable cause) {
        super(message, cause);
    }
}
