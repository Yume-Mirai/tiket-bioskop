package com.uasjava.tiketbioskop.service.impl;

import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uasjava.tiketbioskop.dto.JadwalDTO;
import com.uasjava.tiketbioskop.dto.TiketDTO;
import com.uasjava.tiketbioskop.model.Bioskop;
import com.uasjava.tiketbioskop.model.Film;
import com.uasjava.tiketbioskop.model.Jadwal;
import com.uasjava.tiketbioskop.model.Kursi;
import com.uasjava.tiketbioskop.model.Tiket;
import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.repository.KursiRepository;
import com.uasjava.tiketbioskop.repository.TiketRepository;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;
import com.uasjava.tiketbioskop.service.TiketService;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TiketService {
    private final TransaksiRepository transaksiRepository;
    private final KursiRepository kursiRepository;
    private final ModelMapper modelMapper;
    private final TiketRepository tiketRepository;
    
    
    @Override
    public TiketDTO create(TiketDTO dto) {
       Transaksi transaksi = transaksiRepository.findById(dto.getTransaksiId())
        .orElseThrow(() -> new NoSuchElementException("Film tidak ditemukan"));
    Kursi kursi = kursiRepository.findById(dto.getKursiId())
        .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan"));

    Tiket tiket = Tiket.builder()
        .transaksi(transaksi)
        .kursi(kursi)
        .harga(dto.getHarga())
        .build();

    return modelMapper.map(tiketRepository.save(tiket), TiketDTO.class);
    }

    @Override
    public TiketDTO update(Long id, TiketDTO dto) {
        Tiket tiket = tiketRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Jadwal tidak ditemukan"));

    Transaksi transaksi = transaksiRepository.findById(dto.getTransaksiId())
        .orElseThrow(() -> new NoSuchElementException("Film tidak ditemukan"));
    Kursi kursi = kursiRepository.findById(dto.getKursiId())
        .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan"));

    tiket.setTransaksi(transaksi);
    tiket.setKursi(kursi);
    tiket.setHarga(dto.getHarga());

    return modelMapper.map(tiketRepository.save(tiket), TiketDTO.class);
    }

    @Override
    public void delete(Long id) {
        tiketRepository.deleteById(id);
    }


    @Override
    public TiketDTO getById(Long id) {
        return modelMapper.map(tiketRepository.findById(id).orElseThrow(), TiketDTO.class);
    }

    @Override
    public Page<TiketDTO> getAll(Pageable pageable) {
        return tiketRepository.findAll(pageable).map(j -> modelMapper.map(j, TiketDTO.class));
    }
    
}