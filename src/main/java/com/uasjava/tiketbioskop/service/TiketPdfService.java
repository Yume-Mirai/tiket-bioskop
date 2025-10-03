package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.exception.ResourceNotFoundException;
import com.uasjava.tiketbioskop.model.Tiket;
import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.repository.TiketRepository;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TiketPdfService {

    private final TransaksiRepository transaksiRepository;
    private final TiketRepository tiketRepository;

    public byte[] generateTiketPdf(Long transaksiId) throws IOException {
        try {
            log.info("Generate tiket PDF untuk transaksi ID: {}", transaksiId);

            Transaksi transaksi = transaksiRepository.findById(transaksiId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaksi", "id", transaksiId));

            if (transaksi.getStatus() != Transaksi.StatusTransaksi.LUNAS) {
                throw new IllegalStateException("Hanya transaksi yang sudah lunas yang dapat generate tiket");
            }

            List<Tiket> tiketList = tiketRepository.findByTransaksiId(transaksiId);

            if (tiketList.isEmpty()) {
                throw new ResourceNotFoundException("Tiket tidak ditemukan untuk transaksi ID: " + transaksiId);
            }

            StringBuilder tiketContent = new StringBuilder();

            // Header Tiket
            tiketContent.append("=".repeat(60)).append("\n");
            tiketContent.append("           TIKET BIOSKOP\n");
            tiketContent.append("=".repeat(60)).append("\n\n");

            // Informasi Transaksi
            tiketContent.append("Kode Pembayaran: ").append(transaksi.getKodePembayaran()).append("\n");
            tiketContent.append("Tanggal Transaksi: ").append(transaksi.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("\n");
            tiketContent.append("Status: ").append(transaksi.getStatus().name()).append("\n");
            tiketContent.append("Metode Pembayaran: ").append(transaksi.getMetodePembayaran()).append("\n");
            tiketContent.append("Total Harga: Rp ").append(transaksi.getTotalHarga()).append("\n\n");

            // Informasi Jadwal
            tiketContent.append("Detail Film:\n");
            tiketContent.append("- Judul: ").append(transaksi.getJadwal().getFilm().getJudul()).append("\n");
            tiketContent.append("- Genre: ").append(transaksi.getJadwal().getFilm().getGenre()).append("\n");
            tiketContent.append("- Durasi: ").append(transaksi.getJadwal().getFilm().getDurasi()).append(" menit\n");
            tiketContent.append("- Bioskop: ").append(transaksi.getJadwal().getBioskop().getNama()).append("\n");
            tiketContent.append("- Lokasi: ").append(transaksi.getJadwal().getBioskop().getLokasi()).append("\n");
            tiketContent.append("- Tanggal: ").append(transaksi.getJadwal().getTanggal().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("\n");
            tiketContent.append("- Jam: ").append(transaksi.getJadwal().getJam().format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n\n");

            // Detail Kursi
            tiketContent.append("Kursi yang dipesan:\n");
            for (Tiket tiket : tiketList) {
                tiketContent.append("- ").append(tiket.getKursi().getNomor())
                    .append(" (").append(tiket.getKursi().getTipe()).append(")")
                    .append(" - Rp ").append(tiket.getHarga()).append("\n");
            }

            tiketContent.append("\n");
            tiketContent.append("=".repeat(60)).append("\n");
            tiketContent.append("Simpan tiket ini sebagai bukti pembayaran\n");
            tiketContent.append("Tiket tidak dapat diuangkan kembali\n");
            tiketContent.append("Terima kasih telah menggunakan layanan kami!\n");
            tiketContent.append("=".repeat(60)).append("\n");

            log.info("Berhasil generate tiket PDF untuk transaksi ID: {}", transaksiId);
            return tiketContent.toString().getBytes();

        } catch (Exception e) {
            log.error("Error saat generate tiket PDF untuk transaksi ID {}: {}", transaksiId, e.getMessage(), e);
            throw new IOException("Gagal generate tiket PDF", e);
        }
    }

    public void exportTiketToPdf(Long transaksiId, HttpServletResponse response) throws IOException {
        try {
            byte[] tiketData = generateTiketPdf(transaksiId);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"tiket-" + transaksiId + ".pdf\"");
            response.setContentLength(tiketData.length);

            response.getOutputStream().write(tiketData);
            response.getOutputStream().flush();

            log.info("Berhasil export tiket PDF untuk transaksi ID: {}", transaksiId);

        } catch (IOException e) {
            log.error("Error saat export tiket PDF untuk transaksi ID {}: {}", transaksiId, e.getMessage(), e);
            throw e;
        }
    }
}