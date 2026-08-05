package com.foodwings.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Structured error body returned by {@link GlobalExceptionHandler}.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Builder.Default
    private boolean success = false;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
}
