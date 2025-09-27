package com.uasjava.tiketbioskop.repository;

import com.uasjava.tiketbioskop.model.Film;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    List<Film> findByGenreIgnoreCase(String genre);
    Page<Film> findByGenreIgnoreCase(String genre, Pageable pageable);
    Film findByJudul(String judul);
    Page<Film> findByJudulContainingIgnoreCase(String judul, Pageable pageable);
    Page<Film> findByStatus(Film.StatusFilm status, Pageable pageable);
}
