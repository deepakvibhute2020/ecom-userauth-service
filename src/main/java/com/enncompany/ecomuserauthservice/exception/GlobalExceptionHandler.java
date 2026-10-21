package com.enncompany.ecomuserauthservice.exception;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Handler methods are invoked by Spring MVC reflectively, so the IDE reports them as unused.
@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

  public record ErrorResponse(Instant timestamp, int status, String error, String message) {}

  @ExceptionHandler(UserInfoNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserInfoNotFound(UserInfoNotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.joining("; "));
    return build(HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleUnreadable() {
    return build(HttpStatus.BAD_REQUEST, "Malformed request body");
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter: " + ex.getName());
  }

  private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message));
  }
}
