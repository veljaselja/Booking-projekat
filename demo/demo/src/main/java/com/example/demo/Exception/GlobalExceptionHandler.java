package com.example.demo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApiException.class)
    public ErrorResponse handleApi(ApiException ex) {
        return new ErrorResponse(Instant.now().toString(), 400, ex.getMessage());
    }

    public static class ErrorResponse {
        public String timestamp;
        public int status;
        public String message;

        public ErrorResponse(String timestamp, int status, String message) {
            this.timestamp = timestamp;
            this.status = status;
            this.message = message;
        }
    }
}
