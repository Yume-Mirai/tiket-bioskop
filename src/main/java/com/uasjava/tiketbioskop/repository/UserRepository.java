package com.uasjava.tiketbioskop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uasjava.tiketbioskop.model.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    // boolean existsByUsername(String username);
    // boolean existsByEmail(String email);
    Optional<Users> findById(int userId);
    Optional<Users> findById(Long userId);

    // Search methods
    Page<Users> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);

    // Filter methods
    Page<Users> findByStatus(Boolean status, Pageable pageable);
}
