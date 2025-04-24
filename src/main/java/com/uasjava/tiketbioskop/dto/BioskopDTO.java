package com.uasjava.tiketbioskop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BioskopDTO {
    private Long id;

    @NotBlank(message = "Nama tidak boleh kosong")
    private String nama;

    @NotBlank(message = "Lokasi tidak boleh kosong")
    private String lokasi;
}
