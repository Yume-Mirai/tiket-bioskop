package com.uasjava.tiketbioskop.dto;

import com.uasjava.tiketbioskop.model.Film.StatusFilm;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmRequestDTO {

    @NotBlank(message = "Judul film tidak boleh kosong")
    @Size(min = 1, max = 100, message = "Judul film harus antara 1-100 karakter")
    private String judul;

    @NotBlank(message = "Genre tidak boleh kosong")
    @Size(min = 1, max = 50, message = "Genre harus antara 1-50 karakter")
    private String genre;

    @Min(value = 1, message = "Durasi minimal 1 menit")
    @Max(value = 500, message = "Durasi maksimal 500 menit")
    private int durasi;

    @NotBlank(message = "Sinopsis tidak boleh kosong")
    @Size(min = 10, message = "Sinopsis minimal 10 karakter")
    private String sinopsis;

    @NotBlank(message = "Cast tidak boleh kosong")
    @Size(min = 3, message = "Cast minimal 3 karakter")
    private String cast;

    private MultipartFile poster;

    @Pattern(regexp = "^(https?://.*)?$", message = "URL trailer tidak valid")
    @Size(max = 500, message = "URL trailer maksimal 500 karakter")
    private String trailerUrl;

    private StatusFilm status;
}
