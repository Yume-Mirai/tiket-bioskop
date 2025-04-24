package com.uasjava.tiketbioskop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmailDto {
    private String to;
    private String subject;
    private String body;
}
