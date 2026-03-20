// FILE: src/main/java/com/workshop/exception/RegistrationCancelledException.java
package com.workshop.exception;

public class RegistrationCancelledException extends RuntimeException {
    public RegistrationCancelledException(String message) {
        super(message);
    }
}
