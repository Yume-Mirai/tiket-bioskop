package com.uasjava.tiketbioskop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uasjava.tiketbioskop.model.OtpToken;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByEmailOrderByIdDesc(String email);
}

