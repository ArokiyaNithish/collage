// FILE: src/main/java/com/workshop/exception/PaymentAlreadyDoneException.java
package com.workshop.exception;

public class PaymentAlreadyDoneException extends RuntimeException {
    public PaymentAlreadyDoneException(String message) {
        super(message);
    }
}
