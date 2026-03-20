// FILE: src/main/java/com/workshop/exception/WorkshopNotFoundException.java
package com.workshop.exception;

public class WorkshopNotFoundException extends RuntimeException {
    public WorkshopNotFoundException(String message) {
        super(message);
    }
}
