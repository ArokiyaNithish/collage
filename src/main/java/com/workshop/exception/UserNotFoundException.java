// FILE: src/main/java/com/workshop/exception/UserNotFoundException.java
package com.workshop.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
