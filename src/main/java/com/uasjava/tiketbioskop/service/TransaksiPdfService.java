package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.exception.ResourceNotFoundException;
import com.uasjava.tiketbioskop.model.Transaksi;
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
public class TransaksiPdfService {

    private final TransaksiRepository transaksiRepository;

    public byte[] generateLaporanPdf() throws IOException {
        try {
            log.info("Memulai generate laporan PDF transaksi");

            List<Transaksi> transaksiList = transaksiRepository.findAll();

            if (transaksiList.isEmpty()) {
                log.warn("Tidak ada data transaksi untuk generate PDF");
                throw new ResourceNotFoundException("Tidak ada data transaksi untuk generate laporan PDF");
            }

            // Untuk implementasi PDF yang lebih modern, kita akan menggunakan iText 8 atau library lain
            // Untuk sementara, return struktur data yang akan digunakan untuk generate PDF
            StringBuilder pdfContent = new StringBuilder();
            pdfContent.append("LAPORAN TRANSAKSI TIKET BIOSKOP\n");
            pdfContent.append("Generated at: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n\n");

            pdfContent.append(String.format("%-20s %-30s %-15s %-15s %-15s %-20s%n",
                "Username", "Film", "Metode Bayar", "Total Harga", "Status", "Waktu Transaksi"));
            pdfContent.append("-".repeat(130)).append("\n");

            for (Transaksi transaksi : transaksiList) {
                pdfContent.append(String.format("%-20s %-30s %-15s Rp %-12.0f %-15s %-20s%n",
                    transaksi.getUsers().getUsername(),
                    truncateString(transaksi.getJadwal().getFilm().getJudul(), 28),
                    truncateString(transaksi.getMetodePembayaran(), 13),
                    (double) transaksi.getTotalHarga(),
                    transaksi.getStatus().name(),
                    transaksi.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                ));
            }

            pdfContent.append("\nTotal Transaksi: ").append(transaksiList.size()).append(" items");

            log.info("Berhasil generate laporan PDF dengan {} transaksi", transaksiList.size());
            return pdfContent.toString().getBytes();

        } catch (Exception e) {
            log.error("Error saat generate laporan PDF: {}", e.getMessage(), e);
            throw new IOException("Gagal generate laporan PDF", e);
        }
    }

    public void exportLaporanToPdf(HttpServletResponse response) throws IOException {
        try {
            byte[] pdfData = generateLaporanPdf();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"laporan-transaksi.pdf\"");
            response.setContentLength(pdfData.length);

            response.getOutputStream().write(pdfData);
            response.getOutputStream().flush();

            log.info("Berhasil export laporan PDF ke response");

        } catch (IOException e) {
            log.error("Error saat export PDF ke response: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 2) + "..";
    }
}
