package com.uasjava.tiketbioskop.controller;

import com.uasjava.tiketbioskop.service.TransaksiPdfService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("admin/api/laporan")
@RequiredArgsConstructor
public class LaporanController {

    private final TransaksiPdfService transaksiPdfService;

    @GetMapping("/transaksi/pdf")
    @Operation(summary = "Menampilkan data transaksi dalam bentuk pdf")
    public void downloadLaporanPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=laporan_transaksi.pdf";
        response.setHeader(headerKey, headerValue);

        transaksiPdfService.exportLaporanToPdf(response);
    }
}
