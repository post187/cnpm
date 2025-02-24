package com.example.demo.Exception;

import com.example.demo.dto.request.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<APIResponse> handlingRuntimeException(RuntimeException exception) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<APIResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                APIResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<APIResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        APIResponse apiResponse = new APIResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String key = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(key);
        } catch (IllegalArgumentException e) {

        }
        APIResponse apiResponse = new APIResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }
}
