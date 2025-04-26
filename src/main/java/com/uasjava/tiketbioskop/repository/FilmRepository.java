package com.uasjava.tiketbioskop.repository;

import com.uasjava.tiketbioskop.model.Film;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    List<Film> findByGenreIgnoreCase(String genre);
}
