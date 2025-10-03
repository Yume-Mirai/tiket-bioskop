package com.uasjava.tiketbioskop.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s tidak ditemukan dengan %s: '%s'", resource, field, value));
    }
}