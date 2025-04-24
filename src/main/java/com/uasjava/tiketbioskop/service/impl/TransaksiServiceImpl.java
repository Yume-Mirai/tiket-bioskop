package com.uasjava.tiketbioskop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;
import com.uasjava.tiketbioskop.dto.UserCredentialsDto;
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
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.service.EmailService;
import com.uasjava.tiketbioskop.service.TransaksiService;
import com.uasjava.tiketbioskop.service.helper.AuthHelperService;

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
    @Autowired
    private EmailService emailService;

    @Override
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request) {

        Users user = authHelperService.getCurrentUser();
        Jadwal jadwal = jadwalRepository.findById(request.getJadwalId()).orElseThrow();

        List<Kursi> kursiList = kursiRepository.findAllById(request.getKursiIdList());
        int total = kursiList.size() * 50000; // Harga contoh

        String kode = UUID.randomUUID().toString();
        LocalDateTime expired = LocalDateTime.now().plusHours(1);

        Transaksi transaksi = transaksiRepository.save(
                Transaksi.builder()
                        .users(user)
                        .jadwal(jadwal)
                        .totalHarga(total)
                        .metodePembayaran(request.getMetodePembayaran())
                        .kodePembayaran(kode)
                        .status(StatusTransaksi.PENDING)
                        .expiredAt(LocalDateTime.now().plusMinutes(5))
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
                .orElseThrow();

        if (LocalDateTime.now().isAfter(transaksi.getExpiredAt()))
            return false;

        transaksi.setStatus(Transaksi.StatusTransaksi.LUNAS);
        transaksiRepository.save(transaksi);

        Context context = new Context();
        context.setVariable("username", transaksi.getUsers().getUsername());
        context.setVariable("judul", transaksi.getJadwal().getFilm().getJudul());
        context.setVariable("bioskop", transaksi.getJadwal().getBioskop().getNama());
        context.setVariable("jadwal", transaksi.getJadwal().getTanggal() + " " + transaksi.getJadwal().getJam());
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
            e.printStackTrace();
            // Optionally, log the error or handle it as needed
        }

        return true;
    }
}
