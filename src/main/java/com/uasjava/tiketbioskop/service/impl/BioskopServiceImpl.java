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
public class BioskopServiceImpl implements BioskopService {
    private final BioskopRepository bioskopRepository;
    private final ModelMapper modelMapper;

    @Override
    public BioskopDTO create(BioskopDTO dto) {
        Bioskop bioskop = Bioskop.builder()
                .nama(dto.getNama())
                .lokasi(dto.getLokasi())
                .build();
        return modelMapper.map(bioskopRepository.save(bioskop), BioskopDTO.class);
    }

    @Override
    public BioskopDTO update(Long id, BioskopDTO dto) {
        Bioskop bioskop = bioskopRepository.findById(id).orElseThrow();
        bioskop.setNama(dto.getNama());
        bioskop.setLokasi(dto.getLokasi());
        return modelMapper.map(bioskopRepository.save(bioskop), BioskopDTO.class);
    }

    @Override
    public void delete(Long id) {
        bioskopRepository.deleteById(id);
    }

    @Override
    public BioskopDTO getById(Long id) {
        return modelMapper.map(bioskopRepository.findById(id).orElseThrow(), BioskopDTO.class);
    }

    @Override
    public Page<BioskopDTO> getAll(Pageable pageable) {
        return bioskopRepository.findAll(pageable).map(b -> modelMapper.map(b, BioskopDTO.class));
    }
}
