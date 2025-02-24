package com.example.demo.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999, "UNCATEGORIZED_EXCEPTION.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(0001, "Invalid message.", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed.", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed.", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Password not true", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do  not permission", HttpStatus.FORBIDDEN)
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;

    }
}
