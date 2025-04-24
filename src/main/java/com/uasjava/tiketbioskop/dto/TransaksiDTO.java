package com.uasjava.tiketbioskop.dto;

import java.util.List;

import lombok.Data;

@Data
public class TransaksiDTO {
    private Long id;
    private String username;
    private String film;
    private String jadwal;
    private int totalHarga;
    private String status;
    private List<String> kursi;
}


