package com.laxer.jalloc;

public class OutOfMemoryException extends RuntimeException {
    public OutOfMemoryException(String message) {
        super(message);
    }
}
