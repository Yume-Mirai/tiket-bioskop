package com.uasjava.tiketbioskop.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.uasjava.tiketbioskop.model.Transaksi;
import com.uasjava.tiketbioskop.repository.TransaksiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransaksiPdfService {

    private final TransaksiRepository transaksiRepository;

    public void exportLaporanToPdf(HttpServletResponse response) throws IOException {
        List<Transaksi> transaksiList = transaksiRepository.findAll();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
        Paragraph title = new Paragraph("Laporan Transaksi Tiket Bioskop", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // space

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{2.5f, 2f, 2f, 2f, 2.5f, 2.5f});

        // Header
        addTableHeader(table);

        // Data
        for (Transaksi transaksi : transaksiList) {
            table.addCell(transaksi.getUsers().getUsername());
            table.addCell(transaksi.getJadwal().getFilm().getJudul());
            table.addCell(transaksi.getMetodePembayaran());
            table.addCell(String.valueOf(transaksi.getTotalHarga()));
            table.addCell(transaksi.getStatus().name());
            table.addCell(transaksi.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        }

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Stream.of("Username", "Film", "Metode Pembayaran", "Total Harga", "Status", "Waktu Transaksi")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(Color.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle, headFont));
                    table.addCell(header);
                });
    }
}
