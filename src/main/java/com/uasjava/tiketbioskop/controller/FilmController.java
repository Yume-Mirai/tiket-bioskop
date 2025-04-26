package com.uasjava.tiketbioskop.controller;

import com.uasjava.tiketbioskop.dto.FilmResponseDTO;
import com.uasjava.tiketbioskop.model.Film;
import com.uasjava.tiketbioskop.model.Film.StatusFilm;
import com.uasjava.tiketbioskop.service.FilmService;
import com.uasjava.tiketbioskop.util.ImageUtils;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import com.uasjava.tiketbioskop.repository.FilmRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
// @RequestMapping("/api/film")
@Tag(name = "Film Controller", description = "Upload Film dengan Gambar")
public class FilmController {

    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private FilmService filmService;

    @GetMapping("all/film")
    public ResponseEntity<List<FilmResponseDTO>> getAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/all/film/{id}")
    public ResponseEntity<FilmResponseDTO> getFilmById(@PathVariable Long id) {
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

     @GetMapping("/all/film/{id}/poster")
    public ResponseEntity<byte[]> getPoster(@PathVariable Long id) {
        Optional<Film> filmOpt = filmRepository.findById(id);
        if (filmOpt.isEmpty() || filmOpt.get().getPoster() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] image = ImageUtils.decompressImage(filmOpt.get().getPoster());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // atau IMAGE_PNG, tergantung file asli
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/admin/film",consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFilm(
            @RequestParam("judul") String judul,
            @RequestParam("genre") String genre,
            @RequestParam("durasi") int durasi,
            @RequestParam("sinopsis") String sinopsis,
            @RequestParam("cast") String cast,
            @RequestParam("poster") MultipartFile poster,
            @RequestParam(value = "trailerUrl", required = false) String trailerUrl,
            @RequestParam(value = "status", required = false) Film.StatusFilm status
    ) throws IOException {

        Film savedFilm = filmService.saveFilm(
                judul, genre, durasi, sinopsis, cast, poster, trailerUrl, status
        );

        return ResponseEntity.ok(savedFilm);
    }

    @PutMapping(value = "/admin/film/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FilmResponseDTO> updateFilm(
            @PathVariable Long id,
            @RequestParam String judul,
            @RequestParam String genre,
            @RequestParam int durasi,
            @RequestParam String sinopsis,
            @RequestParam String cast,
            @RequestParam(required = false) String trailerUrl,
            @RequestParam StatusFilm status,
            @RequestPart(required = false) MultipartFile poster
    ) throws IOException {
        return ResponseEntity.ok(filmService.updateFilm(id, judul, genre, durasi, sinopsis, cast, trailerUrl, status, poster));
    }

    @DeleteMapping("/admin/film/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }
}
