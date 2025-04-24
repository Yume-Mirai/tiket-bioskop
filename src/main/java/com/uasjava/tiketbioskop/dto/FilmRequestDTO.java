package com.uasjava.tiketbioskop.dto;

import com.uasjava.tiketbioskop.model.Film.StatusFilm;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FilmRequestDTO {
    private String judul;
    private String genre;
    private int durasi;
    private String sinopsis;
    private String cast;
    private MultipartFile poster;
    private String trailerUrl;
    private StatusFilm status;
}
