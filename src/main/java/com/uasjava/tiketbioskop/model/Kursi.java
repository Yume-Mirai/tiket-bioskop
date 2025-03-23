package com.uasjava.tiketbioskop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "kursi")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Kursi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bioskop_id", nullable = false)
    private Bioskop bioskop;

    @Column(nullable = false, length = 10)
    private String nomor;

    @Enumerated(EnumType.STRING)
    private TipeKursi tipe = TipeKursi.REGULER;

    public enum TipeKursi {
        REGULER, VIP, VVIP
    }
}

