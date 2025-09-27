package com.uasjava.tiketbioskop.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bioskop")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Bioskop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nama;

    @Column(nullable = false, length = 255)
    private String lokasi;

    @OneToMany(mappedBy = "bioskop", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Jadwal> jadwalList = new ArrayList<>();

}
