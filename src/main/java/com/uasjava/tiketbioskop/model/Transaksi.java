package com.uasjava.tiketbioskop.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "transaksi")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Transaksi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "jadwal_id", nullable = false)
    private Jadwal jadwal;

    @Column(nullable = false)
    private int totalHarga;

    @Enumerated(EnumType.STRING)
    private StatusTransaksi status = StatusTransaksi.PENDING;

    @Column(nullable = false, length = 50)
    private String metodePembayaran;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "transaksi", cascade = CascadeType.ALL)
    private List<Tiket> tiketList = new ArrayList<>();

    public enum StatusTransaksi {
        PENDING, LUNAS, DIBATALKAN
    }
}

