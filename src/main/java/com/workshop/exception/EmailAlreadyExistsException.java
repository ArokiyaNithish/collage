// FILE: src/main/java/com/workshop/exception/EmailAlreadyExistsException.java
package com.workshop.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
