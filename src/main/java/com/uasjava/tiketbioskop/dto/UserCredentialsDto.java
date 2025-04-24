package com.uasjava.tiketbioskop.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UserCredentialsDto {
    private Integer userId;
    private String username;
}
