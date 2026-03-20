// FILE: src/main/java/com/workshop/exception/InvalidPaymentException.java
package com.workshop.exception;

public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String message) {
        super(message);
    }
}
