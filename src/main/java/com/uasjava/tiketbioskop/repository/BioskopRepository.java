package com.uasjava.tiketbioskop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uasjava.tiketbioskop.model.Bioskop;

public interface BioskopRepository extends JpaRepository<Bioskop, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByName(String name) to find a bioskop by its name
    
}
