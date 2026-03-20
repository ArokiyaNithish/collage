// FILE: src/main/java/com/workshop/exception/SelfRegistrationException.java
package com.workshop.exception;

public class SelfRegistrationException extends RuntimeException {
    public SelfRegistrationException(String message) {
        super(message);
    }
}
