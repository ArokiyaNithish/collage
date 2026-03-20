// FILE: src/main/java/com/workshop/exception/InvalidTokenException.java
package com.workshop.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
