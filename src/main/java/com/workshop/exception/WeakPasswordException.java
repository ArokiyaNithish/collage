// FILE: src/main/java/com/workshop/exception/WeakPasswordException.java
package com.workshop.exception;

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException(String message) {
        super(message);
    }
}
