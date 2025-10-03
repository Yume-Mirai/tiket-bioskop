package com.uasjava.tiketbioskop.repository;

import com.uasjava.tiketbioskop.model.Kursi;
import com.uasjava.tiketbioskop.model.Tiket;
import com.uasjava.tiketbioskop.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TiketRepository extends JpaRepository<Tiket, Long> {
    // boolean existsByKursi(Kursi kursi);
    List<Tiket> findByTransaksi(Transaksi transaksi);
    List<Tiket> findByTransaksiId(Long transaksiId);
}