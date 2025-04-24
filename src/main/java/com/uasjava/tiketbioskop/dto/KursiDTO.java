package com.uasjava.tiketbioskop.dto;

import com.uasjava.tiketbioskop.model.Kursi.TipeKursi;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KursiDTO {
    private Long id;
    private Long bioskopId;

    @NotBlank(message = "Nomor kursi tidak boleh kosong")
    private String nomor;

    private TipeKursi tipe = TipeKursi.REGULER;
}
