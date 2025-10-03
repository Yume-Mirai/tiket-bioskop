package com.uasjava.tiketbioskop.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransaksiDTO {
    private Long id;

    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Judul film tidak boleh kosong")
    private String filmJudul;

    @NotBlank(message = "Nama bioskop tidak boleh kosong")
    private String bioskopNama;

    private String jadwal; // formatted tanggal dan jam

    @Min(value = 0, message = "Total harga minimal 0")
    private int totalHarga;

    @NotBlank(message = "Status tidak boleh kosong")
    @Pattern(regexp = "^(PENDING|LUNAS|DIBATALKAN)$",
             message = "Status harus PENDING, LUNAS, atau DIBATALKAN")
    private String status;

    @NotBlank(message = "Metode pembayaran tidak boleh kosong")
    private String metodePembayaran;

    @NotBlank(message = "Kode pembayaran tidak boleh kosong")
    private String kodePembayaran;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @NotEmpty(message = "Daftar kursi tidak boleh kosong")
    private List<String> kursi;

    // Additional useful fields
    private String filmGenre;
    private int filmDurasi;
    private String lokasiBioskop;
}


