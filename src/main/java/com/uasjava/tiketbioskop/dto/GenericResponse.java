package com.uasjava.tiketbioskop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GenericResponse<T> {
    private boolean success;
    private String message;
    private T data;
}

