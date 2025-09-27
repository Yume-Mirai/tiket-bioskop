package com.uasjava.tiketbioskop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.model.Transaksi.StatusTransaksi;
import com.uasjava.tiketbioskop.model.Users;

public interface TransaksiRepository extends JpaRepository<Transaksi, Long> {

    // Custom query methods can be defined here if needed
    // For example, findByUserId or findByMovieId
    Optional<Transaksi> findByKodePembayaran(String kodePembayaran);

    List<Transaksi> findByStatus(StatusTransaksi pending);

    List<Transaksi> findByStatusAndExpiredAtBefore(StatusTransaksi pending, LocalDateTime now);

    Page<Transaksi> findByUsers(Users users, Pageable pageable);
    Page<Transaksi> findByUsersAndStatus(Users users, StatusTransaksi status, Pageable pageable);
    Page<Transaksi> findByUsersAndKodePembayaranContainingIgnoreCase(Users users, String kodePembayaran, Pageable pageable);
}
