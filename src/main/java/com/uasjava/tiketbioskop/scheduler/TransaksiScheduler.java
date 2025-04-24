package com.uasjava.tiketbioskop.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransaksiScheduler {

    private final TransaksiRepository transaksiRepository;

    @Scheduled(fixedRate = 60000) // tiap 1 menit cek
    public void batalkanTransaksiYangKadaluarsa() {
        List<Transaksi> list = transaksiRepository.findByStatusAndExpiredAtBefore(
            Transaksi.StatusTransaksi.PENDING, LocalDateTime.now()
        );

        for (Transaksi transaksi : list) {
            transaksi.setStatus(Transaksi.StatusTransaksi.DIBATALKAN);
        }

        transaksiRepository.saveAll(list);

        if (!list.isEmpty()) {
            System.out.println("Expired " + list.size() + " transaksi.");
        }
    }
}
