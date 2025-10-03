package com.uasjava.tiketbioskop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO untuk response login yang berisi accessToken dan informasi user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private String accessToken;

    private Integer id;

    private String username;

    private String email;

    private String nomor;

    private LocalDate tanggal_lahir;

    private Boolean status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String role;

    private LocalDateTime expiresAt;
}