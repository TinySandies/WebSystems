package com.tinysand.system.errors;

public class ArticleException extends RuntimeException{
    public ArticleException(String message) {
        super(message);
    }

    public ArticleException(Throwable cause) {
        super(cause);
    }

    public ArticleException(String message,
                         Throwable cause) {
        super(message, cause);
    }
}
