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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class JadwalServiceImpl implements JadwalService {
    private final JadwalRepository jadwalRepository;
    private final FilmRepository filmRepository;
    private final BioskopRepository bioskopRepository;
    private final ModelMapper modelMapper;

    @Override
public JadwalDTO create(JadwalDTO dto) {
    Film film = filmRepository.findById(dto.getFilmId())
        .orElseThrow(() -> new NoSuchElementException("Film tidak ditemukan"));
    Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId())
        .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan"));

    Jadwal jadwal = Jadwal.builder()
        .film(film)
        .bioskop(bioskop)
        .tanggal(dto.getTanggal())
        .jam(dto.getJam())
        .build();

    return modelMapper.map(jadwalRepository.save(jadwal), JadwalDTO.class);
}

@Override
public JadwalDTO update(Long id, JadwalDTO dto) {
    Jadwal jadwal = jadwalRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Jadwal tidak ditemukan"));

    Film film = filmRepository.findById(dto.getFilmId())
        .orElseThrow(() -> new NoSuchElementException("Film tidak ditemukan"));
    Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId())
        .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan"));

    jadwal.setFilm(film);
    jadwal.setBioskop(bioskop);
    jadwal.setTanggal(dto.getTanggal());
    jadwal.setJam(dto.getJam());

    return modelMapper.map(jadwalRepository.save(jadwal), JadwalDTO.class);
}


    @Override
    public void delete(Long id) {
        jadwalRepository.deleteById(id);
    }

    @Override
    public JadwalDTO getById(Long id) {
        return modelMapper.map(jadwalRepository.findById(id).orElseThrow(), JadwalDTO.class);
    }

    @Override
    public Page<JadwalDTO> getAll(Pageable pageable) {
        return jadwalRepository.findAll(pageable).map(j -> modelMapper.map(j, JadwalDTO.class));
    }
}