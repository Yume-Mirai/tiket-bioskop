package com.uasjava.tiketbioskop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uasjava.tiketbioskop.model.Bioskop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BioskopRepository extends JpaRepository<Bioskop, Long> {
    Bioskop findByNama(String nama);
    Page<Bioskop> findByNamaContainingIgnoreCase(String nama, Pageable pageable);
    Page<Bioskop> findByLokasiContainingIgnoreCase(String lokasi, Pageable pageable);
}
