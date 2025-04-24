package com.uasjava.tiketbioskop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "film")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String judul;

    @Column(nullable = false, length = 50)
    private String genre;

    @Column(nullable = false)
    private int durasi; // dalam menit

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sinopsis;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String cast;

    @Column(nullable = true, columnDefinition = "LONGBLOB", length = 10485760)
    @Lob
    private byte[] poster;

    @Column(nullable = true)
    private String trailerUrl;

    @Enumerated(EnumType.STRING)
    private StatusFilm status = StatusFilm.TAYANG;

    public enum StatusFilm {
        TAYANG, SEGERA_TAYANG
    }
}
