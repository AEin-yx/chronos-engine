package com.chronos.chronos_engine.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.ZonedDateTime;
import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Bean Validation Errors - Can have MULTIPLE errors per field
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        // Collect ALL validation errors
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            errors.computeIfAbsent(fieldName, k -> new ArrayList<>())
                    .add(errorMessage);
        });

        // Also collect global errors (not tied to specific fields)
        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            errors.computeIfAbsent("_global", k -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        });

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed for " + errors.size() + " field(s)",
                errors
        );

        log.warn("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    // 2. JSON Parsing Errors - Can have multiple parsing issues
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        String message = "Invalid request format";

        Throwable cause = ex.getCause();

        // Handle specific JSON parsing issues
        if (cause instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().isEmpty() ? "unknown"
                    : ife.getPath().get(0).getFieldName();
            Object value = ife.getValue();

            List<String> fieldErrors = new ArrayList<>();

            // Enum parsing error
            if (ife.getTargetType().isEnum()) {
                fieldErrors.add(String.format("Invalid value '%s'. Allowed values: %s",
                        value, Arrays.toString(ife.getTargetType().getEnumConstants())));
            }
            // Boolean parsing error
            else if (ife.getTargetType() == Boolean.class || ife.getTargetType() == boolean.class) {
                fieldErrors.add(String.format("Invalid boolean value '%s'. Use true or false", value));
            }
            // ZonedDateTime parsing error
            else if (ife.getTargetType() == ZonedDateTime.class) {
                fieldErrors.add(String.format("Invalid datetime format '%s'. Use ISO-8601 format (e.g., 2024-11-27T10:30:00Z)", value));
            }
            // Integer/Number parsing error
            else if (Number.class.isAssignableFrom(ife.getTargetType()) ||
                    ife.getTargetType().isPrimitive()) {
                fieldErrors.add(String.format("Invalid number value '%s'", value));
            }
            else {
                fieldErrors.add(String.format("Invalid value '%s' for type %s",
                        value, ife.getTargetType().getSimpleName()));
            }

            errors.put(fieldName, fieldErrors);
        }
        // Missing required fields
        else if (cause instanceof MismatchedInputException mie) {
            String fieldName = mie.getPath().isEmpty() ? "unknown"
                    : mie.getPath().get(0).getFieldName();
            errors.put(fieldName, List.of("Field is required but was not provided"));
        }
        // Generic JSON syntax error
        else if (cause instanceof JsonParseException jpe) {
            errors.put("_json", List.of("Malformed JSON syntax at line " + jpe.getLocation().getLineNr()));
        }
        else {
            errors.put("_request", List.of("Unable to parse request: " + ex.getMostSpecificCause().getMessage()));
        }

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                errors
        );

        log.warn("JSON parsing error: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    // 3. Database Constraint Violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        String message = "Data integrity violation";

        // Try to extract specific constraint information
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (rootMsg != null) {
            // PostgreSQL unique constraint pattern
            if (rootMsg.contains("unique constraint") || rootMsg.contains("duplicate key")) {
                errors.put("_database", List.of("Duplicate value detected. Record already exists."));
            }
            // Foreign key violation
            else if (rootMsg.contains("foreign key constraint")) {
                errors.put("_database", List.of("Referenced record does not exist."));
            }
            // Not null violation
            else if (rootMsg.contains("null value") || rootMsg.contains("not-null")) {
                errors.put("_database", List.of("Required field cannot be null."));
            }
            else {
                errors.put("_database", List.of("Database constraint violation."));
            }
        } else {
            errors.put("_database", List.of("Data integrity constraint violated."));
        }

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                message,
                errors
        );

        log.error("Data integrity violation: {}", rootMsg);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 4. Optimistic Locking
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        Map<String, List<String>> errors = Map.of(
                "_concurrency", List.of("Resource was modified by another user. Please refresh and retry.")
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Concurrent modification detected",
                errors
        );

        log.warn("Optimistic locking failure: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 5. Catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        Map<String, List<String>> errors = Map.of(
                "_error", List.of("An unexpected error occurred. Please contact support if this persists.")
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                errors
        );

        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    //6. Handle validation configuration errors (wrong validator for type)
    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedType(UnexpectedTypeException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        String message = ex.getMessage();

        // Extract field name from error message
        if (message.contains("Check configuration for")) {
            String fieldName = message.substring(message.lastIndexOf("'") + 1, message.length() - 1);
            errors.put(fieldName, List.of("Invalid validation constraint configured for this field type"));
        } else {
            errors.put("_validation", List.of("Validation constraint mismatch: " + message));
        }

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation configuration error",
                errors
        );

        log.error("Validation configuration error: {}", message);
        return ResponseEntity.badRequest().body(response);
    }

    // 7. Wrong URL Path
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethod(){
        Map<String, List<String>> errors = Map.of(
                "error", List.of("The URL is Wrong. Use the Correct URL")
        );
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_GATEWAY.value(),"URL address",errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    //8. Handle No Path
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(){
        Map<String, List<String>> errors = Map.of(
                "error", List.of("The URL couldn't be reached. No Static Resource Found")
        );
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_GATEWAY.value(),"No Static Resource",errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}