package com.uasjava.tiketbioskop.service.impl;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uasjava.tiketbioskop.dto.TiketDTO;
import com.uasjava.tiketbioskop.model.Kursi;
import com.uasjava.tiketbioskop.model.Tiket;
import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.repository.KursiRepository;
import com.uasjava.tiketbioskop.repository.TiketRepository;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;
import com.uasjava.tiketbioskop.service.TiketService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketServiceImpl implements TiketService {
    private final TransaksiRepository transaksiRepository;
    private final KursiRepository kursiRepository;
    private final ModelMapper modelMapper;
    private final TiketRepository tiketRepository;

    @Override
    @Transactional
    public TiketDTO create(TiketDTO dto) {
        log.info("Membuat tiket baru untuk transaksi ID: {} dan kursi ID: {}", dto.getTransaksiId(), dto.getKursiId());

        // Validasi input
        if (dto.getTransaksiId() == null) {
            throw new IllegalArgumentException("Transaksi ID tidak boleh kosong");
        }

        if (dto.getKursiId() == null) {
            throw new IllegalArgumentException("Kursi ID tidak boleh kosong");
        }

        if (dto.getHarga() <= 0) {
            throw new IllegalArgumentException("Harga tiket harus lebih dari 0");
        }

        Transaksi transaksi = transaksiRepository.findById(dto.getTransaksiId())
                .orElseThrow(() -> new NoSuchElementException("Transaksi tidak ditemukan dengan ID: " + dto.getTransaksiId()));

        Kursi kursi = kursiRepository.findById(dto.getKursiId())
                .orElseThrow(() -> new NoSuchElementException("Kursi tidak ditemukan dengan ID: " + dto.getKursiId()));

        // Validasi bahwa kursi dan transaksi kompatibel (kursi di bioskop yang sama dengan jadwal transaksi)
        if (!kursi.getBioskop().getId().equals(transaksi.getJadwal().getBioskop().getId())) {
            throw new IllegalArgumentException("Kursi tidak tersedia untuk bioskop jadwal ini");
        }

        // Check apakah kursi sudah digunakan dalam transaksi yang sama atau transaksi aktif lainnya
        boolean kursiUsed = tiketRepository.findByTransaksi(transaksi).stream()
                .anyMatch(t -> t.getKursi().getId().equals(dto.getKursiId()));

        if (kursiUsed) {
            throw new IllegalArgumentException("Kursi sudah digunakan dalam transaksi ini");
        }

        Tiket tiket = Tiket.builder()
                .transaksi(transaksi)
                .kursi(kursi)
                .harga(dto.getHarga())
                .build();

        Tiket savedTiket = tiketRepository.save(tiket);
        log.info("Tiket berhasil dibuat dengan ID: {}", savedTiket.getId());

        return modelMapper.map(savedTiket, TiketDTO.class);
    }

    @Override
    @Transactional
    public TiketDTO update(Long id, TiketDTO dto) {
        log.info("Mengupdate tiket dengan ID: {}", id);

        Tiket existingTiket = tiketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tiket tidak ditemukan dengan ID: " + id));

        // Validasi input
        if (dto.getTransaksiId() == null) {
            throw new IllegalArgumentException("Transaksi ID tidak boleh kosong");
        }

        if (dto.getKursiId() == null) {
            throw new IllegalArgumentException("Kursi ID tidak boleh kosong");
        }

        if (dto.getHarga() <= 0) {
            throw new IllegalArgumentException("Harga tiket harus lebih dari 0");
        }

        Transaksi transaksi = transaksiRepository.findById(dto.getTransaksiId())
                .orElseThrow(() -> new NoSuchElementException("Transaksi tidak ditemukan dengan ID: " + dto.getTransaksiId()));

        Kursi kursi = kursiRepository.findById(dto.getKursiId())
                .orElseThrow(() -> new NoSuchElementException("Kursi tidak ditemukan dengan ID: " + dto.getKursiId()));

        // Validasi bahwa kursi dan transaksi kompatibel
        if (!kursi.getBioskop().getId().equals(transaksi.getJadwal().getBioskop().getId())) {
            throw new IllegalArgumentException("Kursi tidak tersedia untuk bioskop jadwal ini");
        }

        // Check apakah kursi sudah digunakan dalam tiket lain (kecuali tiket yang sama)
        boolean kursiUsedByOtherTicket = tiketRepository.findAll().stream()
                .anyMatch(t -> !t.getId().equals(id) &&
                              t.getKursi().getId().equals(dto.getKursiId()) &&
                              t.getTransaksi().getId().equals(dto.getTransaksiId()));

        if (kursiUsedByOtherTicket) {
            throw new IllegalArgumentException("Kursi sudah digunakan dalam tiket lain untuk transaksi ini");
        }

        existingTiket.setTransaksi(transaksi);
        existingTiket.setKursi(kursi);
        existingTiket.setHarga(dto.getHarga());

        Tiket updatedTiket = tiketRepository.save(existingTiket);
        log.info("Tiket berhasil diupdate dengan ID: {}", updatedTiket.getId());

        return modelMapper.map(updatedTiket, TiketDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Menghapus tiket dengan ID: {}", id);

        Tiket tiket = tiketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tiket tidak ditemukan dengan ID: " + id));

        tiketRepository.deleteById(id);
        log.info("Tiket berhasil dihapus dengan ID: {}", id);
    }

    @Override
    public TiketDTO getById(Long id) {
        log.debug("Mengambil tiket dengan ID: {}", id);

        Tiket tiket = tiketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tiket tidak ditemukan dengan ID: " + id));

        return modelMapper.map(tiket, TiketDTO.class);
    }

    @Override
    public Page<TiketDTO> getAll(Pageable pageable) {
        log.debug("Mengambil semua tiket dengan pagination");

        return tiketRepository.findAll(pageable)
                .map(t -> modelMapper.map(t, TiketDTO.class));
    }
}