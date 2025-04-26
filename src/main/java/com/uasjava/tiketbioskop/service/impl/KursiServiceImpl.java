package com.uasjava.tiketbioskop.service.impl;

import com.uasjava.tiketbioskop.dto.*;
import com.uasjava.tiketbioskop.model.*;
import com.uasjava.tiketbioskop.repository.*;
import com.uasjava.tiketbioskop.service.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class KursiServiceImpl implements KursiService {
    private final KursiRepository kursiRepository;
    private final BioskopRepository bioskopRepository;
    private final ModelMapper modelMapper;

    @Override
    public KursiDTO create(KursiDTO dto) {
        Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId()).orElseThrow();
        Kursi kursi = Kursi.builder()
                .bioskop(bioskop)
                .nomor(dto.getNomor())
                .tipe(dto.getTipe())
                .build();
        return modelMapper.map(kursiRepository.save(kursi), KursiDTO.class);
    }

    @Override
    public KursiDTO update(Long id, KursiDTO dto) {
        Kursi kursi = kursiRepository.findById(id).orElseThrow();
        Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId()).orElseThrow();
        kursi.setBioskop(bioskop);
        kursi.setNomor(dto.getNomor());
        kursi.setTipe(dto.getTipe());
        return modelMapper.map(kursiRepository.save(kursi), KursiDTO.class);
    }

    @Override
    public void delete(Long id) {
        kursiRepository.deleteById(id);
    }

    @Override
    public KursiDTO getById(Long id) {
        return modelMapper.map(kursiRepository.findById(id).orElseThrow(), KursiDTO.class);
    }

    @Override
    public Page<KursiDTO> getAll(Pageable pageable) {
        return kursiRepository.findAll(pageable).map(k -> modelMapper.map(k, KursiDTO.class));
    }
}
