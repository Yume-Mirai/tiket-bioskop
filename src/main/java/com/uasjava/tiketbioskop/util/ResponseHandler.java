package com.uasjava.tiketbioskop.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> success(String message, Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.OK.value());
        body.put("message", message);
        body.put("data", data);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<Object> error(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
