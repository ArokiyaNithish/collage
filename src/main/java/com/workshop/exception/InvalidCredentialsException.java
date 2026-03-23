// FILE: src/main/java/com/workshop/exception/InvalidCredentialsException.java
package com.workshop.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
