// FILE: src/main/java/com/workshop/exception/AlreadyRegisteredException.java
package com.workshop.exception;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(String message) {
        super(message);
    }
}
