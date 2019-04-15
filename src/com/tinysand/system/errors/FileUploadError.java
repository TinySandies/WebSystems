package com.tinysand.system.errors;

@SuppressWarnings("unused")
public class FileUploadError extends RuntimeException{
    public FileUploadError(String message) {
        super(message);
    }

    public FileUploadError(Throwable cause) {
        super(cause);
    }

    public FileUploadError(String message,
                           Throwable cause) {
        super(message, cause);
    }
}
