package com.uasjava.tiketbioskop.service.impl;

import com.uasjava.tiketbioskop.dto.*;
import com.uasjava.tiketbioskop.model.*;
import com.uasjava.tiketbioskop.repository.*;
import com.uasjava.tiketbioskop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class KursiServiceImpl implements KursiService {
    private final KursiRepository kursiRepository;
    private final BioskopRepository bioskopRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public KursiDTO create(KursiDTO dto) {
        log.info("Membuat kursi baru: {} untuk bioskop ID: {}", dto.getNomor(), dto.getBioskopId());

        // Validasi input
        if (dto.getBioskopId() == null) {
            throw new IllegalArgumentException("Bioskop ID tidak boleh kosong");
        }

        if (dto.getNomor() == null || dto.getNomor().trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor kursi tidak boleh kosong");
        }

        if (dto.getTipe() == null) {
            dto.setTipe(Kursi.TipeKursi.REGULER); // Default tipe
        }

        Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId())
                .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan dengan ID: " + dto.getBioskopId()));

        // Check apakah nomor kursi sudah ada di bioskop yang sama
        List<Kursi> existingKursi = kursiRepository.findByBioskop(bioskop);
        boolean nomorExists = existingKursi.stream()
                .anyMatch(k -> k.getNomor().equalsIgnoreCase(dto.getNomor().trim()));

        if (nomorExists) {
            throw new IllegalArgumentException("Nomor kursi sudah ada di bioskop ini: " + dto.getNomor());
        }

        Kursi kursi = Kursi.builder()
                .bioskop(bioskop)
                .nomor(dto.getNomor().trim().toUpperCase())
                .tipe(dto.getTipe())
                .build();

        Kursi savedKursi = kursiRepository.save(kursi);
        log.info("Kursi berhasil dibuat dengan ID: {}", savedKursi.getId());

        return modelMapper.map(savedKursi, KursiDTO.class);
    }

    @Override
    @Transactional
    public KursiDTO update(Long id, KursiDTO dto) {
        log.info("Mengupdate kursi dengan ID: {}", id);

        Kursi existingKursi = kursiRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Kursi tidak ditemukan dengan ID: " + id));

        // Validasi input
        if (dto.getBioskopId() == null) {
            throw new IllegalArgumentException("Bioskop ID tidak boleh kosong");
        }

        if (dto.getNomor() == null || dto.getNomor().trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor kursi tidak boleh kosong");
        }

        if (dto.getTipe() == null) {
            dto.setTipe(Kursi.TipeKursi.REGULER); // Default tipe
        }

        Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId())
                .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan dengan ID: " + dto.getBioskopId()));

        // Check apakah nomor kursi sudah ada di bioskop yang sama (kecuali kursi yang sama)
        List<Kursi> existingKursiInBioskop = kursiRepository.findByBioskop(bioskop);
        boolean nomorExists = existingKursiInBioskop.stream()
                .anyMatch(k -> !k.getId().equals(id) && k.getNomor().equalsIgnoreCase(dto.getNomor().trim()));

        if (nomorExists) {
            throw new IllegalArgumentException("Nomor kursi sudah ada di bioskop ini: " + dto.getNomor());
        }

        existingKursi.setBioskop(bioskop);
        existingKursi.setNomor(dto.getNomor().trim().toUpperCase());
        existingKursi.setTipe(dto.getTipe());

        Kursi updatedKursi = kursiRepository.save(existingKursi);
        log.info("Kursi berhasil diupdate dengan ID: {}", updatedKursi.getId());

        return modelMapper.map(updatedKursi, KursiDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Menghapus kursi dengan ID: {}", id);

        Kursi kursi = kursiRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Kursi tidak ditemukan dengan ID: " + id));

        // Check jika kursi sedang digunakan dalam tiket aktif
        // Note: Dalam implementasi sebenarnya, perlu dicek dari TiketRepository
        // Untuk sementara, langsung hapus saja dengan asumsi tidak ada tiket aktif

        kursiRepository.deleteById(id);
        log.info("Kursi berhasil dihapus dengan ID: {}", id);
    }

    @Override
    public KursiDTO getById(Long id) {
        log.debug("Mengambil kursi dengan ID: {}", id);

        Kursi kursi = kursiRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Kursi tidak ditemukan dengan ID: " + id));

        return modelMapper.map(kursi, KursiDTO.class);
    }

    @Override
    public Page<KursiDTO> getAll(Pageable pageable) {
        log.debug("Mengambil semua kursi dengan pagination");

        return kursiRepository.findAll(pageable)
                .map(k -> modelMapper.map(k, KursiDTO.class));
    }
}
