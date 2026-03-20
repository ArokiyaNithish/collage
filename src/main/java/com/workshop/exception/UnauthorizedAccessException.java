// FILE: src/main/java/com/workshop/exception/UnauthorizedAccessException.java
package com.workshop.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
