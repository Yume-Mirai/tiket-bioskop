package com.uasjava.tiketbioskop.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TransaksiDTO {
    private Long id;
    private String username;
    private String filmJudul;
    private String bioskopNama;
    private String jadwal;
    private int totalHarga;
    private String status;
    private String metodePembayaran;
    private String kodePembayaran;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private List<String> kursi;
}


