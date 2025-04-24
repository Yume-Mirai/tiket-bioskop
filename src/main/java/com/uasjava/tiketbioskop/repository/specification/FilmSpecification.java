package com.uasjava.tiketbioskop.repository.specification;

import com.uasjava.tiketbioskop.model.Film;
import com.uasjava.tiketbioskop.model.Film.StatusFilm;
import org.springframework.data.jpa.domain.Specification;

public class FilmSpecification {
    public static Specification<Film> hasGenre(String genre) {
        return (root, query, cb) -> genre == null ? null : cb.equal(root.get("genre"), genre);
    }

    public static Specification<Film> hasStatus(StatusFilm status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Film> hasJudulContaining(String keyword) {
        return (root, query, cb) -> keyword == null ? null : cb.like(cb.lower(root.get("judul")), "%" + keyword.toLowerCase() + "%");
    }
}
