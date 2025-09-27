package com.uasjava.tiketbioskop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;
import com.uasjava.tiketbioskop.dto.TransaksiDTO;
import com.uasjava.tiketbioskop.model.Jadwal;
import com.uasjava.tiketbioskop.model.Kursi;
import com.uasjava.tiketbioskop.model.Tiket;
import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.model.Transaksi.StatusTransaksi;
import com.uasjava.tiketbioskop.repository.JadwalRepository;
import com.uasjava.tiketbioskop.repository.KursiRepository;
import com.uasjava.tiketbioskop.repository.TiketRepository;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;
import com.uasjava.tiketbioskop.service.EmailService;
import com.uasjava.tiketbioskop.service.TransaksiService;
import com.uasjava.tiketbioskop.service.helper.AuthHelperService;
import org.springframework.data.domain.Page;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransaksiServiceImpl implements TransaksiService {
    private final JadwalRepository jadwalRepository;
    private final KursiRepository kursiRepository;
    private final TransaksiRepository transaksiRepository;
    private final TiketRepository tiketRepository;
    private final AuthHelperService authHelperService;
    private final EmailService emailService;

    @Override
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request) {

        Users user = authHelperService.getCurrentUser();
        Jadwal jadwal = jadwalRepository.findById(request.getJadwalId()).orElseThrow(() -> new RuntimeException("Jadwal tidak ditemukan"));

        List<Kursi> kursiList = kursiRepository.findAllById(request.getKursiIdList());
        int total = kursiList.size() * 50000; // Harga contoh

        String kode = UUID.randomUUID().toString();
        LocalDateTime expired = LocalDateTime.now().plusMinutes(5);

        Transaksi transaksi = transaksiRepository.save(
                Transaksi.builder()
                        .users(user)
                        .jadwal(jadwal)
                        .totalHarga(total)
                        .metodePembayaran(request.getMetodePembayaran())
                        .kodePembayaran(kode)
                        .status(StatusTransaksi.PENDING)
                        .expiredAt(expired)
                        .build());

        List<Tiket> tiketList = kursiList.stream()
                .map(kursi -> Tiket.builder()
                        .transaksi(transaksi)
                        .kursi(kursi)
                        .harga(50000)
                        .build())
                .collect(Collectors.toList());

        tiketRepository.saveAll(tiketList);
        transaksi.setTiketList(tiketList);

        return CheckoutResponseDTO.builder()
                .transaksiId(transaksi.getId())
                .kodePembayaran(kode)
                .expiredAt(expired)
                .totalHarga(total)
                .metodePembayaran(request.getMetodePembayaran())
                .tiketIdList(tiketList.stream().map(Tiket::getId).collect(Collectors.toList()))
                .build();
    }

    @Override
    public boolean konfirmasiPembayaran(String kodePembayaran) {
        Transaksi transaksi = transaksiRepository.findByKodePembayaran(kodePembayaran)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan"));

        if (transaksi.getStatus() != StatusTransaksi.PENDING) {
            return false; // Sudah dikonfirmasi atau dibatalkan
        }

        if (LocalDateTime.now().isAfter(transaksi.getExpiredAt())) {
            transaksi.setStatus(StatusTransaksi.DIBATALKAN);
            transaksiRepository.save(transaksi);
            return false;
        }

        transaksi.setStatus(StatusTransaksi.LUNAS);
        transaksiRepository.save(transaksi);

        Context context = new Context();
        context.setVariable("username", transaksi.getUsers().getUsername());
        context.setVariable("judul", transaksi.getJadwal().getFilm().getJudul());
        context.setVariable("bioskop", transaksi.getJadwal().getBioskop().getNama());
        context.setVariable("jadwal", transaksi.getJadwal().getTanggal() + " " + transaksi.getJadwal().getJam().toString());
        context.setVariable("kursi", transaksi.getTiketList().stream().map(t -> t.getKursi().getNomor()).toList());
        context.setVariable("totalHarga", transaksi.getTotalHarga());
        context.setVariable("status", transaksi.getStatus());

        try {
            emailService.sendPaymentEmail(
                    transaksi.getUsers().getEmail(),
                    "Tiket Bioskop Anda Berhasil Dipesan!",
                    "email/payment-success",
                    context);
        } catch (MessagingException e) {
             // Log error when email fails to send
             System.err.println("Failed to send email: " + e.getMessage());
         }

        return true;
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
            TransaksiDTO dto = new TransaksiDTO();
            dto.setId(transaksi.getId());
            dto.setTotalHarga(transaksi.getTotalHarga());
            dto.setStatus(transaksi.getStatus().name());
            dto.setMetodePembayaran(transaksi.getMetodePembayaran());
            dto.setKodePembayaran(transaksi.getKodePembayaran());
            dto.setCreatedAt(transaksi.getCreatedAt());
            dto.setExpiredAt(transaksi.getExpiredAt());

            // Set film info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getFilm() != null) {
                dto.setFilmJudul(transaksi.getJadwal().getFilm().getJudul());
            }

            // Set bioskop info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getBioskop() != null) {
                dto.setBioskopNama(transaksi.getJadwal().getBioskop().getNama());
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
            TransaksiDTO dto = new TransaksiDTO();
            dto.setId(transaksi.getId());
            dto.setTotalHarga(transaksi.getTotalHarga());
            dto.setStatus(transaksi.getStatus().name());
            dto.setMetodePembayaran(transaksi.getMetodePembayaran());
            dto.setKodePembayaran(transaksi.getKodePembayaran());
            dto.setCreatedAt(transaksi.getCreatedAt());
            dto.setExpiredAt(transaksi.getExpiredAt());

            // Set film info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getFilm() != null) {
                dto.setFilmJudul(transaksi.getJadwal().getFilm().getJudul());
            }

            // Set bioskop info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getBioskop() != null) {
                dto.setBioskopNama(transaksi.getJadwal().getBioskop().getNama());
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
            TransaksiDTO dto = new TransaksiDTO();
            dto.setId(transaksi.getId());
            dto.setTotalHarga(transaksi.getTotalHarga());
            dto.setStatus(transaksi.getStatus().name());
            dto.setMetodePembayaran(transaksi.getMetodePembayaran());
            dto.setKodePembayaran(transaksi.getKodePembayaran());
            dto.setCreatedAt(transaksi.getCreatedAt());
            dto.setExpiredAt(transaksi.getExpiredAt());

            // Set film info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getFilm() != null) {
                dto.setFilmJudul(transaksi.getJadwal().getFilm().getJudul());
            }

            // Set bioskop info
            if (transaksi.getJadwal() != null && transaksi.getJadwal().getBioskop() != null) {
                dto.setBioskopNama(transaksi.getJadwal().getBioskop().getNama());
            }

            return dto;
        });
    }
}
