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

            // Create PDF document using OpenPDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 50, 50, 50, 50); // Landscape orientation for better table display
            PdfWriter.getInstance(document, baos);

            document.open();

            // Set fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.BOLD);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);

            // Title
            Paragraph title = new Paragraph("LAPORAN TRANSAKSI TIKET BIOSKOP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Generated date
            Paragraph dateInfo = new Paragraph("Generated at: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), normalFont);
            dateInfo.setAlignment(Element.ALIGN_CENTER);
            dateInfo.setSpacingAfter(20);
            document.add(dateInfo);

            // Create table with 6 columns
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 3, 2, 2, 2, 3});
            table.setSpacingAfter(20);

            // Table headers
            String[] headers = {"Username", "Film", "Metode Bayar", "Total Harga", "Status", "Waktu Transaksi"};

            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(new Color(220, 220, 220));
                headerCell.setPadding(8);
                table.addCell(headerCell);
            }

            // Table data
            for (Transaksi transaksi : transaksiList) {
                table.addCell(new Phrase(transaksi.getUsers().getUsername(), normalFont));
                table.addCell(new Phrase(truncateString(transaksi.getJadwal().getFilm().getJudul(), 25), normalFont));
                table.addCell(new Phrase(truncateString(transaksi.getMetodePembayaran(), 12), normalFont));
                table.addCell(new Phrase("Rp " + transaksi.getTotalHarga(), normalFont));
                table.addCell(new Phrase(transaksi.getStatus().name(), normalFont));
                table.addCell(new Phrase(transaksi.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont));
            }

            document.add(table);

            // Summary
            Paragraph summary = new Paragraph("Total Transaksi: " + transaksiList.size() + " items", headerFont);
            summary.setSpacingBefore(15);
            document.add(summary);

            document.close();

            log.info("Berhasil generate laporan PDF dengan {} transaksi", transaksiList.size());
            return baos.toByteArray();

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
