package com.chronos.chronos_engine.exception;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public record ErrorResponse(
        int status,
        String message,
        Map<String, List<String>> fieldErrors,  // Changed to List<String> to hold multiple errors per field
        String timestamp
) {
    public ErrorResponse(int status, String message, Map<String, List<String>> fieldErrors) {
        this(status, message, fieldErrors, ZonedDateTime.now().toString());
    }

    // Helper to create with single errors
    public static ErrorResponse withSingleErrors(int status, String message, Map<String, String> errors) {
        Map<String, List<String>> fieldErrors = errors.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> List.of(e.getValue())
                ));
        return new ErrorResponse(status, message, fieldErrors);
    }
}
