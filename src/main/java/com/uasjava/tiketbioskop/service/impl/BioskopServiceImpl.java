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

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BioskopServiceImpl implements BioskopService {
    private final BioskopRepository bioskopRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BioskopDTO create(BioskopDTO dto) {
        log.info("Membuat bioskop baru: {}", dto.getNama());

        // Validasi nama bioskop tidak duplikat
        if (bioskopRepository.existsByNamaIgnoreCase(dto.getNama())) {
            throw new IllegalArgumentException("Nama bioskop sudah digunakan: " + dto.getNama());
        }

        // Validasi input
        if (dto.getNama() == null || dto.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama bioskop tidak boleh kosong");
        }

        if (dto.getLokasi() == null || dto.getLokasi().trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasi bioskop tidak boleh kosong");
        }

        Bioskop bioskop = Bioskop.builder()
                .nama(dto.getNama().trim())
                .lokasi(dto.getLokasi().trim())
                .build();

        Bioskop savedBioskop = bioskopRepository.save(bioskop);
        log.info("Bioskop berhasil dibuat dengan ID: {}", savedBioskop.getId());

        return modelMapper.map(savedBioskop, BioskopDTO.class);
    }

    @Override
    @Transactional
    public BioskopDTO update(Long id, BioskopDTO dto) {
        log.info("Mengupdate bioskop dengan ID: {}", id);

        Bioskop existingBioskop = bioskopRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan dengan ID: " + id));

        // Validasi nama bioskop tidak duplikat (kecuali nama yang sama)
        if (!existingBioskop.getNama().equalsIgnoreCase(dto.getNama()) &&
            bioskopRepository.existsByNamaIgnoreCase(dto.getNama())) {
            throw new IllegalArgumentException("Nama bioskop sudah digunakan: " + dto.getNama());
        }

        // Validasi input
        if (dto.getNama() == null || dto.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama bioskop tidak boleh kosong");
        }

        if (dto.getLokasi() == null || dto.getLokasi().trim().isEmpty()) {
            throw new IllegalArgumentException("Lokasi bioskop tidak boleh kosong");
        }

        existingBioskop.setNama(dto.getNama().trim());
        existingBioskop.setLokasi(dto.getLokasi().trim());

        Bioskop updatedBioskop = bioskopRepository.save(existingBioskop);
        log.info("Bioskop berhasil diupdate dengan ID: {}", updatedBioskop.getId());

        return modelMapper.map(updatedBioskop, BioskopDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Menghapus bioskop dengan ID: {}", id);

        Bioskop bioskop = bioskopRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan dengan ID: " + id));

        // Check jika bioskop memiliki jadwal aktif
        if (!bioskop.getJadwalList().isEmpty()) {
            throw new IllegalStateException("Tidak dapat menghapus bioskop yang masih memiliki jadwal");
        }

        bioskopRepository.deleteById(id);
        log.info("Bioskop berhasil dihapus dengan ID: {}", id);
    }

    @Override
    public BioskopDTO getById(Long id) {
        log.debug("Mengambil bioskop dengan ID: {}", id);

        Bioskop bioskop = bioskopRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan dengan ID: " + id));

        return modelMapper.map(bioskop, BioskopDTO.class);
    }

    @Override
    public Page<BioskopDTO> getAll(Pageable pageable) {
        log.debug("Mengambil semua bioskop dengan pagination");

        return bioskopRepository.findAll(pageable)
                .map(b -> modelMapper.map(b, BioskopDTO.class));
    }

    @Override
    public Page<BioskopDTO> searchBioskop(String nama, int page, int size, String sortBy, String sortDir) {
        log.info("Mencari bioskop dengan nama: {}", nama);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return bioskopRepository.findByNamaContainingIgnoreCase(nama, pageable)
            .map(b -> modelMapper.map(b, BioskopDTO.class));
    }

    @Override
    public Page<BioskopDTO> filterBioskopByLocation(String lokasi, int page, int size, String sortBy, String sortDir) {
        log.info("Filter bioskop berdasarkan lokasi: {}", lokasi);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return bioskopRepository.findByLokasiContainingIgnoreCase(lokasi, pageable)
            .map(b -> modelMapper.map(b, BioskopDTO.class));
    }
}
