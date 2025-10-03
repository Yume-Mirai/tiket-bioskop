package com.uasjava.tiketbioskop.exception;

/**
 * Exception yang dilempar ketika terjadi kesalahan otorisasi
 */
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}