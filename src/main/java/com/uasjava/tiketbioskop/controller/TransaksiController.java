package com.uasjava.tiketbioskop.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;
import com.uasjava.tiketbioskop.dto.KonfirmasiPembayaranDTO;
import com.uasjava.tiketbioskop.dto.TransaksiDTO;
import com.uasjava.tiketbioskop.service.TransaksiService;

import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/api/transaksi")
@RequiredArgsConstructor
public class TransaksiController {

    private final TransaksiService transaksiService;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@RequestBody CheckoutRequestDTO dto) {
        return ResponseEntity.ok(transaksiService.checkout(dto));
    }

    @PostMapping("/konfirmasi")
    public ResponseEntity<String> konfirmasiPembayaran(@RequestBody KonfirmasiPembayaranDTO dto) {
        boolean sukses = transaksiService.konfirmasiPembayaran(dto.getKodePembayaran());
        return sukses ?
            ResponseEntity.ok("Pembayaran berhasil") :
            ResponseEntity.badRequest().body("Pembayaran gagal atau sudah kadaluarsa");
    }

    @GetMapping("/my-transactions")
    @Operation(summary = "Menampilkan transaksi user dengan pagination dan sorting")
    public ResponseEntity<Page<TransaksiDTO>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(transaksiService.getMyTransactions(page, size, sortBy, sortDir));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter transaksi user berdasarkan status dengan pagination")
    public ResponseEntity<Page<TransaksiDTO>> filterMyTransactionsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(transaksiService.filterMyTransactionsByStatus(status, page, size, sortBy, sortDir));
    }

    @GetMapping("/search")
    @Operation(summary = "Search transaksi user berdasarkan kode pembayaran dengan pagination")
    public ResponseEntity<Page<TransaksiDTO>> searchMyTransactions(
            @RequestParam String kodePembayaran,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(transaksiService.searchMyTransactions(kodePembayaran, page, size, sortBy, sortDir));
    }
}
