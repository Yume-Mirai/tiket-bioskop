package com.uasjava.tiketbioskop.service.impl;

import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;
import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.dto.TransaksiDTO;
import com.uasjava.tiketbioskop.exception.KursiNotAvailableException;
import com.uasjava.tiketbioskop.exception.ResourceNotFoundException;
import com.uasjava.tiketbioskop.model.Jadwal;
import com.uasjava.tiketbioskop.model.Kursi;
import com.uasjava.tiketbioskop.model.Tiket;
import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.repository.JadwalRepository;
import com.uasjava.tiketbioskop.repository.KursiRepository;
import com.uasjava.tiketbioskop.repository.TiketRepository;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;
import com.uasjava.tiketbioskop.service.EmailService;
import com.uasjava.tiketbioskop.service.TransaksiService;
import com.uasjava.tiketbioskop.service.helper.AuthHelperService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransaksiServiceImpl implements TransaksiService {
    private final JadwalRepository jadwalRepository;
    private final KursiRepository kursiRepository;
    private final TransaksiRepository transaksiRepository;
    private final TiketRepository tiketRepository;
    private final AuthHelperService authHelperService;
    private final EmailService emailService;

    @Override
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request) {
        log.info("Memulai proses checkout untuk user dan jadwal ID: {}", request.getJadwalId());

        // Validasi user
        Users user = authHelperService.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User tidak valid atau tidak login");
        }

        // Validasi jadwal
        Jadwal jadwal = jadwalRepository.findById(request.getJadwalId())
                .orElseThrow(() -> new ResourceNotFoundException("Jadwal", "id", request.getJadwalId()));

        // Validasi jadwal masih berlaku
        if (jadwal.getTanggal().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new IllegalStateException("Jadwal sudah tidak berlaku");
        }

        // Validasi kursi tersedia
        List<Kursi> requestedKursi = kursiRepository.findAllById(request.getKursiIdList());
        if (requestedKursi.size() != request.getKursiIdList().size()) {
            throw new KursiNotAvailableException("Beberapa kursi tidak ditemukan");
        }

        // Check kursi yang sudah di-booking (LUNAS)
        List<Kursi> unavailableKursi = kursiRepository.findAvailableKursiForJadwal(
                request.getKursiIdList(),
                jadwal.getBioskop().getId(),
                jadwal.getId()
        );

        if (unavailableKursi.size() != request.getKursiIdList().size()) {
            List<String> unavailableKursiNomor = requestedKursi.stream()
                    .filter(kursi -> !unavailableKursi.contains(kursi))
                    .map(Kursi::getNomor)
                    .collect(Collectors.toList());
            throw new KursiNotAvailableException(
                String.join(", ", unavailableKursiNomor),
                "kursi sudah di-booking"
            );
        }

        // Check kursi yang sedang pending (mencegah double booking)
        List<Kursi> pendingKursi = kursiRepository.findPendingKursiForJadwal(
                request.getKursiIdList(),
                jadwal.getId()
        );

        if (!pendingKursi.isEmpty()) {
            List<String> pendingKursiNomor = pendingKursi.stream()
                    .map(Kursi::getNomor)
                    .collect(Collectors.toList());
            throw new KursiNotAvailableException(
                String.join(", ", pendingKursiNomor),
                "kursi sedang dalam proses booking"
            );
        }

        // Hitung harga berdasarkan tipe kursi
        int totalHarga = calculateTotalHarga(requestedKursi);

        // Generate kode pembayaran unik
        String kodePembayaran = generateUniqueKodePembayaran();

        // Set waktu expired (5 menit)
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

        // Buat transaksi
        Transaksi transaksi = Transaksi.builder()
                .users(user)
                .jadwal(jadwal)
                .totalHarga(totalHarga)
                .metodePembayaran(request.getMetodePembayaran())
                .kodePembayaran(kodePembayaran)
                .status(Transaksi.StatusTransaksi.PENDING)
                .expiredAt(expiredAt)
                .build();

        // Simpan transaksi terlebih dahulu
        transaksi = transaksiRepository.save(transaksi);

        // Buat tiket untuk setiap kursi
        final Transaksi finalTransaksi = transaksi;
        List<Tiket> tiketList = requestedKursi.stream()
                .map(kursi -> {
                    int harga = getHargaByTipe(kursi.getTipe());
                    return Tiket.builder()
                            .transaksi(finalTransaksi)
                            .kursi(kursi)
                            .harga(harga)
                            .build();
                })
                .collect(Collectors.toList());

        tiketList = tiketRepository.saveAll(tiketList);

        log.info("Checkout berhasil untuk transaksi ID: {} dengan kode: {}", transaksi.getId(), kodePembayaran);

        return CheckoutResponseDTO.builder()
                .transaksiId(transaksi.getId())
                .kodePembayaran(kodePembayaran)
                .expiredAt(expiredAt)
                .totalHarga(totalHarga)
                .metodePembayaran(request.getMetodePembayaran())
                .tiketIdList(tiketList.stream().map(Tiket::getId).collect(Collectors.toList()))
                .build();
    }

    private int calculateTotalHarga(List<Kursi> kursiList) {
        return kursiList.stream()
                .mapToInt(kursi -> getHargaByTipe(kursi.getTipe()))
                .sum();
    }

    private int getHargaByTipe(Kursi.TipeKursi tipe) {
        return switch (tipe) {
            case REGULER -> 50000;
            case VIP -> 75000;
            case VVIP -> 100000;
        };
    }

    private String generateUniqueKodePembayaran() {
        String kode;
        int attempts = 0;
        do {
            kode = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Tidak dapat generate kode pembayaran unik");
            }
        } while (transaksiRepository.existsByKodePembayaran(kode));

        return kode;
    }

    @Override
    public boolean konfirmasiPembayaran(String kodePembayaran) {
        log.info("Memproses konfirmasi pembayaran dengan kode: {}", kodePembayaran);

        Transaksi transaksi = transaksiRepository.findByKodePembayaran(kodePembayaran)
                .orElseThrow(() -> new ResourceNotFoundException("Transaksi", "kodePembayaran", kodePembayaran));

        if (transaksi.getStatus() != Transaksi.StatusTransaksi.PENDING) {
            log.warn("Transaksi dengan kode {} sudah diproses dengan status: {}",
                kodePembayaran, transaksi.getStatus());
            return false; // Sudah dikonfirmasi atau dibatalkan
        }

        if (LocalDateTime.now().isAfter(transaksi.getExpiredAt())) {
            log.info("Transaksi dengan kode {} sudah kadaluarsa, membatalkan...", kodePembayaran);
            transaksi.setStatus(Transaksi.StatusTransaksi.DIBATALKAN);
            transaksiRepository.save(transaksi);
            return false;
        }

        // Update status menjadi LUNAS
        transaksi.setStatus(Transaksi.StatusTransaksi.LUNAS);
        transaksiRepository.save(transaksi);

        // Kirim email konfirmasi
        try {
            sendPaymentConfirmationEmail(transaksi);
            log.info("Email konfirmasi berhasil dikirim untuk transaksi: {}", kodePembayaran);
        } catch (MessagingException e) {
            log.error("Gagal mengirim email konfirmasi untuk transaksi {}: {}", kodePembayaran, e.getMessage(), e);
            // Jangan throw exception karena pembayaran sudah berhasil
        }

        return true;
    }

    private void sendPaymentConfirmationEmail(Transaksi transaksi) throws MessagingException {
        Context context = new Context();
        context.setVariable("username", transaksi.getUsers().getUsername());
        context.setVariable("kodePembayaran", transaksi.getKodePembayaran());
        context.setVariable("judul", transaksi.getJadwal().getFilm().getJudul());
        context.setVariable("genre", transaksi.getJadwal().getFilm().getGenre());
        context.setVariable("durasi", transaksi.getJadwal().getFilm().getDurasi());
        context.setVariable("bioskop", transaksi.getJadwal().getBioskop().getNama());
        context.setVariable("lokasi", transaksi.getJadwal().getBioskop().getLokasi());
        context.setVariable("tanggal", transaksi.getJadwal().getTanggal().toString());
        context.setVariable("jam", transaksi.getJadwal().getJam().toString());
        context.setVariable("kursi", transaksi.getTiketList().stream()
            .map(t -> t.getKursi().getNomor() + " (" + t.getKursi().getTipe() + ")")
            .toList());
        context.setVariable("totalHarga", transaksi.getTotalHarga());
        context.setVariable("metodePembayaran", transaksi.getMetodePembayaran());
        context.setVariable("status", transaksi.getStatus());
        context.setVariable("waktuTransaksi", transaksi.getCreatedAt().toString());

        emailService.sendPaymentEmail(
                transaksi.getUsers().getEmail(),
                "Konfirmasi Pembayaran Tiket Bioskop - " + transaksi.getKodePembayaran(),
                "email/payment-success",
                context);
    }

    @Override
    public GenericResponse<String> batalkanTransaksi(Long transaksiId) {
        log.info("User membatalkan transaksi ID: {}", transaksiId);

        Users currentUser = authHelperService.getCurrentUser();
        Transaksi transaksi = transaksiRepository.findById(transaksiId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaksi", "id", transaksiId));

        // Validasi kepemilikan transaksi
        if (transaksi.getUsers().getId() != currentUser.getId()) {
            throw new IllegalStateException("Anda tidak memiliki akses untuk membatalkan transaksi ini");
        }

        // Validasi status transaksi
        if (transaksi.getStatus() != Transaksi.StatusTransaksi.PENDING) {
            return GenericResponse.<String>builder()
                    .success(false)
                    .message("Hanya transaksi dengan status PENDING yang dapat dibatalkan")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // Validasi waktu pembatalan (minimal 30 menit sebelum jadwal)
        if (transaksi.getJadwal().getTanggal().isBefore(LocalDateTime.now().toLocalDate()) ||
            (transaksi.getJadwal().getTanggal().isEqual(LocalDateTime.now().toLocalDate()) &&
             transaksi.getJadwal().getJam().isBefore(LocalDateTime.now().toLocalTime().plusMinutes(30)))) {

            return GenericResponse.<String>builder()
                    .success(false)
                    .message("Transaksi tidak dapat dibatalkan karena jadwal sudah terlalu dekat")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // Update status transaksi
        transaksi.setStatus(Transaksi.StatusTransaksi.DIBATALKAN);
        transaksiRepository.save(transaksi);

        log.info("Transaksi ID: {} berhasil dibatalkan oleh user: {}", transaksiId, currentUser.getUsername());

        return GenericResponse.<String>builder()
                .success(true)
                .message("Transaksi berhasil dibatalkan")
                .data("Transaksi dengan kode " + transaksi.getKodePembayaran() + " telah dibatalkan")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public Page<TransaksiDTO> getMyTransactions(int page, int size, String sortBy, String sortDir) {
        Users currentUser = authHelperService.getCurrentUser();

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return transaksiRepository.findByUsers(currentUser, pageable).map(transaksi -> {
            TransaksiDTO dto = TransaksiDTO.builder()
                    .id(transaksi.getId())
                    .username(currentUser.getUsername())
                    .totalHarga(transaksi.getTotalHarga())
                    .status(transaksi.getStatus().name())
                    .metodePembayaran(transaksi.getMetodePembayaran())
                    .kodePembayaran(transaksi.getKodePembayaran())
                    .createdAt(transaksi.getCreatedAt())
                    .expiredAt(transaksi.getExpiredAt())
                    .kursi(transaksi.getTiketList().stream()
                        .map(tiket -> tiket.getKursi().getNomor() + " (" + tiket.getKursi().getTipe() + ")")
                        .collect(Collectors.toList()))
                    .build();

            // Set film info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getFilm() != null) {
                dto.setFilmJudul(transaksi.getJadwal().getFilm().getJudul());
                dto.setFilmGenre(transaksi.getJadwal().getFilm().getGenre());
                dto.setFilmDurasi(transaksi.getJadwal().getFilm().getDurasi());
            }

            // Set bioskop info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getBioskop() != null) {
                dto.setBioskopNama(transaksi.getJadwal().getBioskop().getNama());
                dto.setLokasiBioskop(transaksi.getJadwal().getBioskop().getLokasi());
            }

            // Set jadwal info
            if (transaksi.getJadwal() != null) {
                dto.setJadwal(transaksi.getJadwal().getTanggal().toString() + " " +
                             transaksi.getJadwal().getJam().toString());
            }

            return dto;
        });
    }

    @Override
    public Page<TransaksiDTO> filterMyTransactionsByStatus(String status, int page, int size, String sortBy, String sortDir) {
        Users currentUser = authHelperService.getCurrentUser();
        Transaksi.StatusTransaksi statusEnum = Transaksi.StatusTransaksi.valueOf(status.toUpperCase());

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return transaksiRepository.findByUsersAndStatus(currentUser, statusEnum, pageable).map(transaksi -> {
            TransaksiDTO dto = TransaksiDTO.builder()
                    .id(transaksi.getId())
                    .username(currentUser.getUsername())
                    .totalHarga(transaksi.getTotalHarga())
                    .status(transaksi.getStatus().name())
                    .metodePembayaran(transaksi.getMetodePembayaran())
                    .kodePembayaran(transaksi.getKodePembayaran())
                    .createdAt(transaksi.getCreatedAt())
                    .expiredAt(transaksi.getExpiredAt())
                    .kursi(transaksi.getTiketList().stream()
                        .map(tiket -> tiket.getKursi().getNomor() + " (" + tiket.getKursi().getTipe() + ")")
                        .collect(Collectors.toList()))
                    .build();

            // Set film info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getFilm() != null) {
                dto.setFilmJudul(transaksi.getJadwal().getFilm().getJudul());
                dto.setFilmGenre(transaksi.getJadwal().getFilm().getGenre());
                dto.setFilmDurasi(transaksi.getJadwal().getFilm().getDurasi());
            }

            // Set bioskop info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getBioskop() != null) {
                dto.setBioskopNama(transaksi.getJadwal().getBioskop().getNama());
                dto.setLokasiBioskop(transaksi.getJadwal().getBioskop().getLokasi());
            }

            // Set jadwal info
            if (transaksi.getJadwal() != null) {
                dto.setJadwal(transaksi.getJadwal().getTanggal().toString() + " " +
                             transaksi.getJadwal().getJam().toString());
            }

            return dto;
        });
    }

    @Override
    public Page<TransaksiDTO> searchMyTransactions(String kodePembayaran, int page, int size, String sortBy, String sortDir) {
        Users currentUser = authHelperService.getCurrentUser();

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            page, size,
            sortDir.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending()
        );

        return transaksiRepository.findByUsersAndKodePembayaranContainingIgnoreCase(currentUser, kodePembayaran, pageable).map(transaksi -> {
            TransaksiDTO dto = TransaksiDTO.builder()
                    .id(transaksi.getId())
                    .username(currentUser.getUsername())
                    .totalHarga(transaksi.getTotalHarga())
                    .status(transaksi.getStatus().name())
                    .metodePembayaran(transaksi.getMetodePembayaran())
                    .kodePembayaran(transaksi.getKodePembayaran())
                    .createdAt(transaksi.getCreatedAt())
                    .expiredAt(transaksi.getExpiredAt())
                    .kursi(transaksi.getTiketList().stream()
                        .map(tiket -> tiket.getKursi().getNomor() + " (" + tiket.getKursi().getTipe() + ")")
                        .collect(Collectors.toList()))
                    .build();

            // Set film info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getFilm() != null) {
                dto.setFilmJudul(transaksi.getJadwal().getFilm().getJudul());
                dto.setFilmGenre(transaksi.getJadwal().getFilm().getGenre());
                dto.setFilmDurasi(transaksi.getJadwal().getFilm().getDurasi());
            }

            // Set bioskop info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getBioskop() != null) {
                dto.setBioskopNama(transaksi.getJadwal().getBioskop().getNama());
                dto.setLokasiBioskop(transaksi.getJadwal().getBioskop().getLokasi());
            }

            // Set jadwal info
            if (transaksi.getJadwal() != null) {
                dto.setJadwal(transaksi.getJadwal().getTanggal().toString() + " " +
                             transaksi.getJadwal().getJam().toString());
            }

            return dto;
        });
    }
}
