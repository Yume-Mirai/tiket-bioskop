package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JadwalService {
    JadwalDTO create(JadwalDTO dto);
    JadwalDTO update(Long id, JadwalDTO dto);
    void delete(Long id);
    JadwalDTO getById(Long id);
    Page<JadwalDTO> getAll(Pageable pageable);
}