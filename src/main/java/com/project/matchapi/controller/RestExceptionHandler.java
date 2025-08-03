package com.project.matchapi.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.project.matchapi.service.exception.MatchNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.debug("Failed to parse request: {}", ex.getMessage(), ex);

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .findFirst()
                .orElse("Invalid request.");
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMatchNotFound(MatchNotFoundException ex) {
        log.debug("MatchNotFoundException: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEnum(HttpMessageNotReadableException ex) {
        log.debug("Failed to read request body: {}", ex.getMessage(), ex);

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            var allowed = String.join(", ", Arrays.stream(ife.getTargetType().getEnumConstants()).map(Object::toString).toList());
            var field = ife.getPath().isEmpty() ? "unknown" : ife.getPath().getFirst().getFieldName();
            var msg = "Invalid value for " + field + ": " + ife.getValue() + ". Allowed: " + allowed;
            return ResponseEntity.badRequest().body(new ErrorResponse(msg));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request body."));
    }

    public record ErrorResponse(String message) {
    }
}
