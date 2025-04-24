package com.uasjava.tiketbioskop.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebResponse<T> {
    private int status;
    private String message;
    private T data;
}
