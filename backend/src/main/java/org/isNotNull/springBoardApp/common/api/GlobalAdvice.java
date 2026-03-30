package org.isNotNull.springBoardApp.common.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Converts exceptions to stable JSON responses for the frontend.
 *
 * Example:
 * Validation failures become a single message string.
 */
@RestControllerAdvice
public final class GlobalAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorJson> invalid(final MethodArgumentNotValidException failure) {
        final String text = failure.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorJson(text));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorJson> wrongCredentials(final BadCredentialsException failure) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorJson("Invalid credentials"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorJson> denied(final AccessDeniedException failure) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorJson("Access denied"));
    }

    @ExceptionHandler(MissingEntityException.class)
    public ResponseEntity<ErrorJson> missing(final MissingEntityException failure) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorJson(failure.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorJson> state(final IllegalStateException failure) {
        return ResponseEntity.badRequest().body(new ErrorJson(failure.getMessage()));
    }
}
