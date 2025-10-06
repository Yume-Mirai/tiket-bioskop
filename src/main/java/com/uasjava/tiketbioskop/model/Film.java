package com.uasjava.tiketbioskop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @NotBlank(message = "Judul film tidak boleh kosong")
    @Size(min = 1, max = 100, message = "Judul film harus antara 1-100 karakter")
    private String judul;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Genre tidak boleh kosong")
    @Size(min = 1, max = 50, message = "Genre harus antara 1-50 karakter")
    private String genre;

    @Column(nullable = false)
    @Min(value = 1, message = "Durasi minimal 1 menit")
    @Max(value = 500, message = "Durasi maksimal 500 menit")
    private int durasi; // dalam menit

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Sinopsis tidak boleh kosong")
    @Size(min = 10, message = "Sinopsis minimal 10 karakter")
    private String sinopsis;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Cast tidak boleh kosong")
    @Size(min = 3, message = "Cast minimal 3 karakter")
    private String cast;

    @Column(nullable = true, columnDefinition = "LONGBLOB")
    @Lob
    private byte[] poster;

    @Column(nullable = true, length = 500)
    @Pattern(regexp = "^(https?://.*)?$", message = "URL trailer tidak valid")
    private String trailerUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusFilm status = StatusFilm.TAYANG;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Jadwal> jadwalList = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum StatusFilm {
        TAYANG, SEGERA_TAYANG
    }
}
