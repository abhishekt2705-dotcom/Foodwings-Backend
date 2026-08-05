package com.foodwings.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralised handling of exceptions across all controllers, mapping them to the
 * standard {@link ErrorResponse} body with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAppRuntime(RuntimeException ex, HttpServletRequest request) {
        // Map application-specific runtime exceptions to HTTP statuses by simple name to
        // avoid compile-time references that some IDEs (Eclipse JDT) may not resolve.
        String name = ex.getClass().getSimpleName();
        switch (name) {
            case "ResourceNotFoundException":
                return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
            case "BadRequestException":
            case "ValidationException":
                return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
            case "DuplicateResourceException":
                return build(HttpStatus.CONFLICT, ex.getMessage(), request);
            case "UnauthorizedException":
                return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
            default:
                // Not an application-specific exception we handle here — rethrow so other
                // handlers (or the generic handler) can process it.
                throw ex;
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUpload(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "Uploaded file exceeds the maximum allowed size", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse body = new ErrorResponse(false,
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                fieldErrors,
                LocalDateTime.now());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() == null ? "An unexpected error occurred" : ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(false,
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                null,
                LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
