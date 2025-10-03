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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JadwalServiceImpl implements JadwalService {
    private final JadwalRepository jadwalRepository;
    private final FilmRepository filmRepository;
    private final BioskopRepository bioskopRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public JadwalDTO create(JadwalDTO dto) {
        log.info("Membuat jadwal baru untuk film ID: {} dan bioskop ID: {}", dto.getFilmId(), dto.getBioskopId());

        // Validasi input
        if (dto.getFilmId() == null) {
            throw new IllegalArgumentException("Film ID tidak boleh kosong");
        }

        if (dto.getBioskopId() == null) {
            throw new IllegalArgumentException("Bioskop ID tidak boleh kosong");
        }

        if (dto.getTanggal() == null) {
            throw new IllegalArgumentException("Tanggal tidak boleh kosong");
        }

        if (dto.getJam() == null) {
            throw new IllegalArgumentException("Jam tidak boleh kosong");
        }

        // Validasi tanggal tidak boleh di masa lalu
        if (dto.getTanggal().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal jadwal tidak boleh di masa lalu");
        }

        // Validasi jam dalam rentang yang wajar (08:00 - 23:59)
        if (dto.getJam().isBefore(LocalTime.of(8, 0)) || dto.getJam().isAfter(LocalTime.of(23, 59))) {
            throw new IllegalArgumentException("Jam tayang harus antara 08:00 dan 23:59");
        }

        Film film = filmRepository.findById(dto.getFilmId())
                .orElseThrow(() -> new NoSuchElementException("Film tidak ditemukan dengan ID: " + dto.getFilmId()));

        Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId())
                .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan dengan ID: " + dto.getBioskopId()));

        // Check konflik jadwal
        if (jadwalRepository.existsConflictingSchedule(bioskop, dto.getTanggal(), dto.getJam(), null)) {
            throw new IllegalArgumentException("Sudah ada jadwal di bioskop yang sama pada waktu tersebut");
        }

        Jadwal jadwal = Jadwal.builder()
                .film(film)
                .bioskop(bioskop)
                .tanggal(dto.getTanggal())
                .jam(dto.getJam())
                .build();

        Jadwal savedJadwal = jadwalRepository.save(jadwal);
        log.info("Jadwal berhasil dibuat dengan ID: {}", savedJadwal.getId());

        return modelMapper.map(savedJadwal, JadwalDTO.class);
    }

    @Override
    @Transactional
    public JadwalDTO update(Long id, JadwalDTO dto) {
        log.info("Mengupdate jadwal dengan ID: {}", id);

        Jadwal existingJadwal = jadwalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Jadwal tidak ditemukan dengan ID: " + id));

        // Validasi input
        if (dto.getFilmId() == null) {
            throw new IllegalArgumentException("Film ID tidak boleh kosong");
        }

        if (dto.getBioskopId() == null) {
            throw new IllegalArgumentException("Bioskop ID tidak boleh kosong");
        }

        if (dto.getTanggal() == null) {
            throw new IllegalArgumentException("Tanggal tidak boleh kosong");
        }

        if (dto.getJam() == null) {
            throw new IllegalArgumentException("Jam tidak boleh kosong");
        }

        // Validasi tanggal tidak boleh di masa lalu
        if (dto.getTanggal().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal jadwal tidak boleh di masa lalu");
        }

        // Validasi jam dalam rentang yang wajar
        if (dto.getJam().isBefore(LocalTime.of(8, 0)) || dto.getJam().isAfter(LocalTime.of(23, 59))) {
            throw new IllegalArgumentException("Jam tayang harus antara 08:00 dan 23:59");
        }

        Film film = filmRepository.findById(dto.getFilmId())
                .orElseThrow(() -> new NoSuchElementException("Film tidak ditemukan dengan ID: " + dto.getFilmId()));

        Bioskop bioskop = bioskopRepository.findById(dto.getBioskopId())
                .orElseThrow(() -> new NoSuchElementException("Bioskop tidak ditemukan dengan ID: " + dto.getBioskopId()));

        // Check konflik jadwal (kecuali untuk jadwal yang sama)
        if (jadwalRepository.existsConflictingSchedule(bioskop, dto.getTanggal(), dto.getJam(), id)) {
            throw new IllegalArgumentException("Sudah ada jadwal di bioskop yang sama pada waktu tersebut");
        }

        existingJadwal.setFilm(film);
        existingJadwal.setBioskop(bioskop);
        existingJadwal.setTanggal(dto.getTanggal());
        existingJadwal.setJam(dto.getJam());

        Jadwal updatedJadwal = jadwalRepository.save(existingJadwal);
        log.info("Jadwal berhasil diupdate dengan ID: {}", updatedJadwal.getId());

        return modelMapper.map(updatedJadwal, JadwalDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Menghapus jadwal dengan ID: {}", id);

        Jadwal jadwal = jadwalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Jadwal tidak ditemukan dengan ID: " + id));

        // Check jika jadwal memiliki transaksi aktif
        // Note: Dalam implementasi sebenarnya, perlu dicek dari TransaksiRepository
        // Untuk sementara, langsung hapus saja

        jadwalRepository.deleteById(id);
        log.info("Jadwal berhasil dihapus dengan ID: {}", id);
    }

    @Override
    public JadwalDTO getById(Long id) {
        log.debug("Mengambil jadwal dengan ID: {}", id);

        Jadwal jadwal = jadwalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Jadwal tidak ditemukan dengan ID: " + id));

        return modelMapper.map(jadwal, JadwalDTO.class);
    }

    @Override
    public Page<JadwalDTO> getAll(Pageable pageable) {
        log.debug("Mengambil semua jadwal dengan pagination");

        return jadwalRepository.findAll(pageable)
                .map(j -> modelMapper.map(j, JadwalDTO.class));
    }
}