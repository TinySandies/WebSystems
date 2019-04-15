package com.tinysand.system.errors;

//登录时密码或账号错误
public class EntranceError extends RuntimeException {
    public EntranceError(String message) {
        super(message);
    }

    public EntranceError(Throwable cause) {
        super(cause);
    }

    public EntranceError(String message,
                         Throwable cause) {
        super(message, cause);
    }
}
