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
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.awt.Color;

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

            // Create PDF document using OpenPDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Set font
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);

            // Header Tiket
            Paragraph header = new Paragraph("TIKET BIOSKOP", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            document.add(header);

            // Border line
            addLineSeparator(document);

            // Informasi Transaksi
            Paragraph transaksiInfo = new Paragraph("INFORMASI TRANSAKSI", headerFont);
            transaksiInfo.setSpacingAfter(10);
            document.add(transaksiInfo);

            PdfPTable transaksiTable = new PdfPTable(2);
            transaksiTable.setWidthPercentage(100);
            transaksiTable.setWidths(new float[]{1, 2});
            transaksiTable.setSpacingAfter(15);

            addTableRow(transaksiTable, "Kode Pembayaran:", transaksi.getKodePembayaran(), normalFont);
            addTableRow(transaksiTable, "Tanggal Transaksi:", transaksi.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont);
            addTableRow(transaksiTable, "Status:", transaksi.getStatus().name(), normalFont);
            addTableRow(transaksiTable, "Metode Pembayaran:", transaksi.getMetodePembayaran(), normalFont);
            addTableRow(transaksiTable, "Total Harga:", "Rp " + transaksi.getTotalHarga(), normalFont);

            document.add(transaksiTable);

            // Informasi Jadwal
            Paragraph jadwalInfo = new Paragraph("DETAIL FILM & JADWAL", headerFont);
            jadwalInfo.setSpacingAfter(10);
            document.add(jadwalInfo);

            PdfPTable jadwalTable = new PdfPTable(2);
            jadwalTable.setWidthPercentage(100);
            jadwalTable.setWidths(new float[]{1, 2});
            jadwalTable.setSpacingAfter(15);

            addTableRow(jadwalTable, "Judul Film:", transaksi.getJadwal().getFilm().getJudul(), normalFont);
            addTableRow(jadwalTable, "Genre:", transaksi.getJadwal().getFilm().getGenre(), normalFont);
            addTableRow(jadwalTable, "Durasi:", transaksi.getJadwal().getFilm().getDurasi() + " menit", normalFont);
            addTableRow(jadwalTable, "Bioskop:", transaksi.getJadwal().getBioskop().getNama(), normalFont);
            addTableRow(jadwalTable, "Lokasi:", transaksi.getJadwal().getBioskop().getLokasi(), normalFont);
            addTableRow(jadwalTable, "Tanggal:", transaksi.getJadwal().getTanggal().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), normalFont);
            addTableRow(jadwalTable, "Jam:", transaksi.getJadwal().getJam().format(DateTimeFormatter.ofPattern("HH:mm")), normalFont);

            document.add(jadwalTable);

            // Detail Kursi
            Paragraph kursiInfo = new Paragraph("KURSI YANG DIPESAN", headerFont);
            kursiInfo.setSpacingAfter(10);
            document.add(kursiInfo);

            PdfPTable kursiTable = new PdfPTable(3);
            kursiTable.setWidthPercentage(100);
            kursiTable.setWidths(new float[]{2, 1, 2});
            kursiTable.setSpacingAfter(15);

            // Header tabel kursi
            PdfPCell cell = new PdfPCell(new Phrase("Nomor Kursi", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            kursiTable.addCell(cell);

            cell = new PdfPCell(new Phrase("Tipe", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            kursiTable.addCell(cell);

            cell = new PdfPCell(new Phrase("Harga", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            kursiTable.addCell(cell);

            // Data kursi
            for (Tiket tiket : tiketList) {
                kursiTable.addCell(new Phrase(tiket.getKursi().getNomor(), normalFont));
                kursiTable.addCell(new Phrase(tiket.getKursi().getTipe().name(), normalFont));
                kursiTable.addCell(new Phrase("Rp " + tiket.getHarga(), normalFont));
            }

            document.add(kursiTable);

            // Footer
            addLineSeparator(document);

            Paragraph footer = new Paragraph("Simpan tiket ini sebagai bukti pembayaran yang sah.\n" +
                    "Tiket tidak dapat diuangkan kembali.\n" +
                    "Terima kasih telah menggunakan layanan kami!", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

            log.info("Berhasil generate tiket PDF untuk transaksi ID: {}", transaksiId);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error saat generate tiket PDF untuk transaksi ID {}: {}", transaksiId, e.getMessage(), e);
            throw new IOException("Gagal generate tiket PDF", e);
        }
    }

    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator line = new LineSeparator();
        line.setOffset(-5);
        document.add(new Chunk(line));
        document.add(new Chunk("\n"));
    }

    private void addTableRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(5);
        table.addCell(valueCell);
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