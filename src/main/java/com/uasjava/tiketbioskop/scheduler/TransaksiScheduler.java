package com.uasjava.tiketbioskop.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransaksiScheduler {

    private final TransaksiRepository transaksiRepository;

    @Scheduled(fixedRate = 60000) // tiap 1 menit cek
    @Transactional
    public void batalkanTransaksiYangKadaluarsa() {
        try {
            log.info("Menjalankan scheduler untuk membatalkan transaksi yang kadaluarsa...");

            List<Transaksi> expiredTransactions = transaksiRepository.findByStatusAndExpiredAtBefore(
                Transaksi.StatusTransaksi.PENDING, LocalDateTime.now()
            );

            if (expiredTransactions.isEmpty()) {
                log.debug("Tidak ada transaksi yang kadaluarsa");
                return;
            }

            // Update status transaksi menjadi DIBATALKAN
            for (Transaksi transaksi : expiredTransactions) {
                transaksi.setStatus(Transaksi.StatusTransaksi.DIBATALKAN);
                log.debug("Membatalkan transaksi ID: {} dengan kode pembayaran: {}",
                    transaksi.getId(), transaksi.getKodePembayaran());
            }

            transaksiRepository.saveAll(expiredTransactions);

            log.info("Berhasil membatalkan {} transaksi yang kadaluarsa", expiredTransactions.size());

        } catch (Exception e) {
            log.error("Error saat menjalankan scheduler pembatalan transaksi kadaluarsa: {}", e.getMessage(), e);
        }
    }
}
