package com.uasjava.tiketbioskop.service.impl;

import com.uasjava.tiketbioskop.dto.FilmResponseDTO;
import com.uasjava.tiketbioskop.exception.ResourceNotFoundException;
import com.uasjava.tiketbioskop.model.Film;
import com.uasjava.tiketbioskop.repository.FilmRepository;
import com.uasjava.tiketbioskop.service.FilmService;
import com.uasjava.tiketbioskop.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    @Autowired
    private final FilmRepository filmRepository;

    @Override
    public Film saveFilm(String judul, String genre, int durasi, String sinopsis, String cast,
                        MultipartFile poster, String trailerUrl, Film.StatusFilm status) throws IOException {
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

    @Override
    public Page<FilmResponseDTO> getAllFilms(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return filmRepository.findAll(pageRequest).map(this::toDto);
    }

    @Override
    public List<FilmResponseDTO> getAllFilms() {
        return filmRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public FilmResponseDTO getFilmById(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film", "id", id));
        return toDto(film);
    }

    @Override
    public FilmResponseDTO updateFilm(Long id, String judul, String genre, int durasi, String sinopsis,
                                     String cast, String trailerUrl, Film.StatusFilm status,
                                     MultipartFile poster) throws IOException {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film dengan ID " + id + " tidak ditemukan"));
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

    @Override
    public void deleteFilm(Long id) {
        filmRepository.deleteById(id);
    }

    @Override
    public Page<FilmResponseDTO> getFilmsByGenre(String genre, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return filmRepository.findByGenreIgnoreCase(genre, pageRequest).map(this::toDto);
    }

    @Override
    public Page<FilmResponseDTO> searchFilmsByTitle(String title, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return filmRepository.findByJudulContainingIgnoreCase(title, pageRequest).map(this::toDto);
    }

    @Override
    public Page<FilmResponseDTO> filterFilmsByStatus(Film.StatusFilm status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return filmRepository.findByStatus(status, pageRequest).map(this::toDto);
    }

    @Override
    public Page<FilmResponseDTO> advancedSearchFilms(String judul, String genre, Film.StatusFilm status,
                                                    Integer minDurasi, Integer maxDurasi,
                                                    int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Build specification for advanced search
        org.springframework.data.jpa.domain.Specification<Film> spec = (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (judul != null && !judul.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("judul")),
                    "%" + judul.toLowerCase() + "%"));
            }

            if (genre != null && !genre.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")),
                    "%" + genre.toLowerCase() + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (minDurasi != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("durasi"), minDurasi));
            }

            if (maxDurasi != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("durasi"), maxDurasi));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return filmRepository.findAll(spec, pageRequest).map(this::toDto);
    }

    @Override
    public List<FilmResponseDTO> getFilmsByGenre(String genre) {
        List<Film> films = filmRepository.findByGenreIgnoreCase(genre);
        return films.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
        if (film.getPoster() != null) {
            try {
                dto.setPoster(ImageUtils.decompressImage(film.getPoster()));
            } catch (Exception e) {
                // Log error and set poster to null if decompression fails
                System.err.println("Failed to decompress poster for film " + film.getId() + ": " + e.getMessage());
                dto.setPoster(null);
            }
        } else {
            dto.setPoster(null);
        }
        return dto;
    }
}