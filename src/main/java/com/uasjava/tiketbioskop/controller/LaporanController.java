package com.uasjava.tiketbioskop.controller;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.service.ReportService;
import com.uasjava.tiketbioskop.service.TiketPdfService;
import com.uasjava.tiketbioskop.service.TransaksiPdfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/laporan")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Laporan Controller", description = "Generate laporan Excel dan PDF")
public class LaporanController {

    private final ReportService reportService;
    private final TransaksiPdfService transaksiPdfService;
    private final TiketPdfService tiketPdfService;

    @GetMapping("/excel/users")
    @Operation(summary = "Generate laporan Excel data users")
    public ResponseEntity<byte[]> generateExcelReport() {
        try {
            log.info("Generate laporan Excel users");

            byte[] excelData = (byte[]) reportService.generateExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "laporan-users-" +
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx");
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (IOException e) {
            log.error("Error saat generate laporan Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pdf/transaksi")
    @Operation(summary = "Generate laporan PDF semua transaksi")
    public ResponseEntity<byte[]> generateTransaksiPdfReport() {
        try {
            log.info("Generate laporan PDF transaksi");

            byte[] pdfData = transaksiPdfService.generateLaporanPdf();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "laporan-transaksi-" +
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf");
            headers.setContentLength(pdfData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);

        } catch (IOException e) {
            log.error("Error saat generate laporan PDF transaksi: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tiket/pdf/{transaksiId}")
    @Operation(summary = "Generate tiket PDF untuk transaksi tertentu")
    public ResponseEntity<byte[]> generateTiketPdf(@PathVariable Long transaksiId) {
        try {
            log.info("Generate tiket PDF untuk transaksi ID: {}", transaksiId);

            byte[] tiketData = tiketPdfService.generateTiketPdf(transaksiId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "tiket-transaksi-" + transaksiId + ".pdf");
            headers.setContentLength(tiketData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(tiketData);

        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("Error validasi saat generate tiket PDF: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error saat generate tiket PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tiket/download/{transaksiId}")
    @Operation(summary = "Download tiket PDF melalui response stream")
    public void downloadTiketPdf(@PathVariable Long transaksiId, HttpServletResponse response) {
        try {
            log.info("Download tiket PDF untuk transaksi ID: {}", transaksiId);
            tiketPdfService.exportTiketToPdf(transaksiId, response);
        } catch (Exception e) {
            log.error("Error saat download tiket PDF: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
