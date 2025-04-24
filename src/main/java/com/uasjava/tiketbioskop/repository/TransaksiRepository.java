package com.uasjava.tiketbioskop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.model.Transaksi.StatusTransaksi;

public interface TransaksiRepository extends JpaRepository<Transaksi, Long> {

    // Custom query methods can be defined here if needed
    // For example, findByUserId or findByMovieId
    Optional<Transaksi> findByKodePembayaran(String kodePembayaran);

    List<Transaksi> findByStatus(StatusTransaksi pending);

    List<Transaksi> findByStatusAndExpiredAtBefore(StatusTransaksi pending, LocalDateTime now);
}
