package com.uasjava.tiketbioskop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class JadwalDTO {
    private Long id;
    private Long filmId;
    private Long bioskopId;

    @NotNull(message = "Tanggal tidak boleh kosong")
    private LocalDate tanggal;

    @NotNull(message = "Jam tidak boleh kosong")
    @Schema(example = "02:00:00", type = "string", format = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime jam;
}
