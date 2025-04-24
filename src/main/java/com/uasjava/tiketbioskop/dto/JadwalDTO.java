package com.uasjava.tiketbioskop.dto;

import com.uasjava.tiketbioskop.model.Kursi.TipeKursi;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class JadwalDTO {
    private Long id;
    private Long filmId;
    private Long bioskopId;

    @NotNull(message = "Tanggal tidak boleh kosong")
    private LocalDate tanggal;

    @NotNull(message = "Jam tidak boleh kosong")
    private LocalTime jam;
}