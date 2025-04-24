package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface KursiService {
    KursiDTO create(KursiDTO dto);
    KursiDTO update(Long id, KursiDTO dto);
    void delete(Long id);
    KursiDTO getById(Long id);
    Page<KursiDTO> getAll(Pageable pageable);
}
