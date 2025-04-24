package com.uasjava.tiketbioskop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uasjava.tiketbioskop.model.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    // boolean existsByUsername(String username);
    // boolean existsByEmail(String email);
    Optional<Users> findById(int userId);
    Optional<Users> findById(Long userId);
    
}
