package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.*;
import com.uasjava.tiketbioskop.util.WebResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TiketService {
    TiketDTO create(TiketDTO dto);
    TiketDTO update(Long id, TiketDTO dto);
    void delete(Long id);
    TiketDTO getById(Long id);
    Page<TiketDTO> getAll(Pageable pageable);
}