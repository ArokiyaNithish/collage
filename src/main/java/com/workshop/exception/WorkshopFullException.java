// FILE: src/main/java/com/workshop/exception/WorkshopFullException.java
package com.workshop.exception;

public class WorkshopFullException extends RuntimeException {
    public WorkshopFullException(String message) {
        super(message);
    }
}
