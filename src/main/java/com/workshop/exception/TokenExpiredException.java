// FILE: src/main/java/com/workshop/exception/TokenExpiredException.java
package com.workshop.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
