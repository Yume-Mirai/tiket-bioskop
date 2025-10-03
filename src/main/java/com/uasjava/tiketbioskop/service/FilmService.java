package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.FilmResponseDTO;
import com.uasjava.tiketbioskop.model.Film;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilmService {
    Film saveFilm(String judul, String genre, int durasi, String sinopsis, String cast,
                  MultipartFile poster, String trailerUrl, Film.StatusFilm status) throws IOException;

    Page<FilmResponseDTO> getAllFilms(int page, int size, String sortBy, String sortDir);

    List<FilmResponseDTO> getAllFilms();

    FilmResponseDTO getFilmById(Long id);

    FilmResponseDTO updateFilm(Long id, String judul, String genre, int durasi, String sinopsis,
                              String cast, String trailerUrl, Film.StatusFilm status,
                              MultipartFile poster) throws IOException;

    void deleteFilm(Long id);

    Page<FilmResponseDTO> getFilmsByGenre(String genre, int page, int size, String sortBy, String sortDir);

    Page<FilmResponseDTO> searchFilmsByTitle(String title, int page, int size, String sortBy, String sortDir);

    Page<FilmResponseDTO> filterFilmsByStatus(Film.StatusFilm status, int page, int size, String sortBy, String sortDir);

    Page<FilmResponseDTO> advancedSearchFilms(String judul, String genre, Film.StatusFilm status,
                                             Integer minDurasi, Integer maxDurasi,
                                             int page, int size, String sortBy, String sortDir);

    List<FilmResponseDTO> getFilmsByGenre(String genre);
}
