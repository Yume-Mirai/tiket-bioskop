package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.FilmResponseDTO;
import com.uasjava.tiketbioskop.model.Film;
import com.uasjava.tiketbioskop.repository.FilmRepository;
import com.uasjava.tiketbioskop.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;

    public Film saveFilm(String judul,
            String genre,
            int durasi,
            String sinopsis,
            String cast,
            MultipartFile poster,
            String trailerUrl,
            Film.StatusFilm status) throws IOException {
        Film film = Film.builder()
                .judul(judul)
                .genre(genre)
                .durasi(durasi)
                .sinopsis(sinopsis)
                .cast(cast)
                .poster(ImageUtils.compressImage(poster.getBytes()))
                .trailerUrl(trailerUrl)
                .status(status != null ? status : Film.StatusFilm.TAYANG)
                .build();

        return filmRepository.save(film);
    }

    public List<FilmResponseDTO> getAllFilms() {
        return filmRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public FilmResponseDTO getFilmById(Long id) {
        return filmRepository.findById(id).map(this::toDto).orElse(null);
    }

    public FilmResponseDTO updateFilm(Long id, String judul, String genre, int durasi, String sinopsis,
            String cast, String trailerUrl, Film.StatusFilm status,
            MultipartFile poster) throws IOException {
        Film film = filmRepository.findById(id).orElseThrow(() -> new RuntimeException("Film not found"));
        film.setJudul(judul);
        film.setGenre(genre);
        film.setDurasi(durasi);
        film.setSinopsis(sinopsis);
        film.setCast(cast);
        film.setTrailerUrl(trailerUrl);
        film.setStatus(status);

        if (poster != null && !poster.isEmpty()) {
            film.setPoster(ImageUtils.compressImage(poster.getBytes()));
        }

        return toDto(filmRepository.save(film));
    }

    public void deleteFilm(Long id) {
        filmRepository.deleteById(id);
    }

    private FilmResponseDTO toDto(Film film) {
        FilmResponseDTO dto = new FilmResponseDTO();
        dto.setId(film.getId());
        dto.setJudul(film.getJudul());
        dto.setGenre(film.getGenre());
        dto.setDurasi(film.getDurasi());
        dto.setSinopsis(film.getSinopsis());
        dto.setCast(film.getCast());
        dto.setTrailerUrl(film.getTrailerUrl());
        dto.setStatus(film.getStatus());
        dto.setPoster(ImageUtils.decompressImage(film.getPoster()));
        return dto;
    }
}
