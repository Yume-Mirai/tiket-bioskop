package com.uasjava.tiketbioskop.dto;

import com.uasjava.tiketbioskop.model.Film.StatusFilm;
import lombok.Data;

@Data
public class FilmResponseDTO {
    private Long id;
    private String judul;
    private String genre;
    private int durasi;
    private String sinopsis;
    private String cast;
    private byte[] poster; // decompressed
    private String trailerUrl;
    private StatusFilm status;
}
