package com.tinysand.system.errors;

public class AccountError extends RuntimeException{
    public AccountError(String message) {
        super(message);
    }

    public AccountError(Throwable cause) {
        super(cause);
    }

    public AccountError(String message,
                        Throwable cause) {
        super(message, cause);
    }
}
