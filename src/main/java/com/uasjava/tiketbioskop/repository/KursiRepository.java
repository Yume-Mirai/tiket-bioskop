package com.uasjava.tiketbioskop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uasjava.tiketbioskop.model.Kursi;

public interface KursiRepository extends JpaRepository<Kursi, Long> {
    
    
}
