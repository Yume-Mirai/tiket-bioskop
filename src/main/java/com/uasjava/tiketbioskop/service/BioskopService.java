package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface BioskopService {
    BioskopDTO create(BioskopDTO dto);
    BioskopDTO update(Long id, BioskopDTO dto);
    void delete(Long id);
    BioskopDTO getById(Long id);
    Page<BioskopDTO> getAll(Pageable pageable);
    Page<BioskopDTO> searchBioskop(String nama, int page, int size, String sortBy, String sortDir);
    Page<BioskopDTO> filterBioskopByLocation(String lokasi, int page, int size, String sortBy, String sortDir);
}
