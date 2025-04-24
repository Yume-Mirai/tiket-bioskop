package com.uasjava.tiketbioskop.repository;

import com.uasjava.tiketbioskop.model.Kursi;
import com.uasjava.tiketbioskop.model.Tiket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiketRepository extends JpaRepository<Tiket, Long> {
    // boolean existsByKursi(Kursi kursi);
}