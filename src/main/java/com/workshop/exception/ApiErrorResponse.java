// FILE: src/main/java/com/workshop/exception/ApiErrorResponse.java
package com.workshop.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private boolean success;
    private int status;
    private String error;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
    private String path;
}
