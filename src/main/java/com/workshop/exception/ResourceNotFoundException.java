// FILE: src/main/java/com/workshop/exception/ResourceNotFoundException.java
package com.workshop.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
