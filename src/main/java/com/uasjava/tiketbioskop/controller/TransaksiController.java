package com.uasjava.tiketbioskop.controller;

import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;
import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.dto.KonfirmasiPembayaranDTO;
import com.uasjava.tiketbioskop.dto.TransaksiDTO;
import com.uasjava.tiketbioskop.service.TransaksiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transaksi")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaksi Controller", description = "Manajemen transaksi tiket bioskop")
public class TransaksiController {

    private final TransaksiService transaksiService;

    @PostMapping("/checkout")
    @Operation(summary = "Melakukan checkout tiket")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<CheckoutResponseDTO>> checkout(@Valid @RequestBody CheckoutRequestDTO dto) {
        try {
            log.info("User melakukan checkout untuk jadwal ID: {}", dto.getJadwalId());

            CheckoutResponseDTO response = transaksiService.checkout(dto);

            GenericResponse<CheckoutResponseDTO> genericResponse = GenericResponse.<CheckoutResponseDTO>builder()
                    .success(true)
                    .message("Checkout berhasil, silakan lakukan pembayaran dalam 5 menit")
                    .data(response)
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(genericResponse);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Checkout gagal: {}", e.getMessage());

            GenericResponse<CheckoutResponseDTO> errorResponse = GenericResponse.<CheckoutResponseDTO>builder()
                    .success(false)
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("Error saat checkout: {}", e.getMessage(), e);

            GenericResponse<CheckoutResponseDTO> errorResponse = GenericResponse.<CheckoutResponseDTO>builder()
                    .success(false)
                    .message("Terjadi kesalahan saat proses checkout")
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/konfirmasi")
    @Operation(summary = "Konfirmasi pembayaran transaksi")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<String>> konfirmasiPembayaran(@Valid @RequestBody KonfirmasiPembayaranDTO dto) {
        try {
            log.info("User mengkonfirmasi pembayaran dengan kode: {}", dto.getKodePembayaran());

            boolean sukses = transaksiService.konfirmasiPembayaran(dto.getKodePembayaran());

            if (sukses) {
                GenericResponse<String> response = GenericResponse.<String>builder()
                        .success(true)
                        .message("Pembayaran berhasil dikonfirmasi. Tiket akan dikirim ke email Anda.")
                        .data("Transaksi berhasil")
                        .timestamp(LocalDateTime.now())
                        .build();

                return ResponseEntity.ok(response);

            } else {
                GenericResponse<String> response = GenericResponse.<String>builder()
                        .success(false)
                        .message("Pembayaran gagal atau transaksi sudah kadaluarsa")
                        .data("Transaksi gagal")
                        .timestamp(LocalDateTime.now())
                        .build();

                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("Error saat konfirmasi pembayaran: {}", e.getMessage(), e);

            GenericResponse<String> errorResponse = GenericResponse.<String>builder()
                    .success(false)
                    .message("Terjadi kesalahan saat konfirmasi pembayaran")
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/cancel/{transaksiId}")
    @Operation(summary = "Batalkan transaksi pending")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<String>> batalkanTransaksi(@PathVariable Long transaksiId) {
        try {
            log.info("User membatalkan transaksi ID: {}", transaksiId);

            GenericResponse<String> response = transaksiService.batalkanTransaksi(transaksiId);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("Pembatalan transaksi gagal: {}", e.getMessage());

            GenericResponse<String> errorResponse = GenericResponse.<String>builder()
                    .success(false)
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("Error saat pembatalan transaksi: {}", e.getMessage(), e);

            GenericResponse<String> errorResponse = GenericResponse.<String>builder()
                    .success(false)
                    .message("Terjadi kesalahan saat pembatalan transaksi")
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/my-transactions")
    @Operation(summary = "Menampilkan transaksi user dengan pagination dan sorting")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<Page<TransaksiDTO>>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            log.info("User mengambil data transaksi sendiri");
            Page<TransaksiDTO> transactions = transaksiService.getMyTransactions(page, size, sortBy, sortDir);

            GenericResponse<Page<TransaksiDTO>> response = GenericResponse.<Page<TransaksiDTO>>builder()
                    .success(true)
                    .message("Data transaksi berhasil diambil")
                    .data(transactions)
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error saat mengambil data transaksi: {}", e.getMessage(), e);

            GenericResponse<Page<TransaksiDTO>> errorResponse = GenericResponse.<Page<TransaksiDTO>>builder()
                    .success(false)
                    .message("Terjadi kesalahan saat mengambil data transaksi")
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter transaksi user berdasarkan status dengan pagination")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TransaksiDTO>> filterMyTransactionsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            log.info("User filter transaksi berdasarkan status: {} - page: {}, size: {}", status, page, size);
            return ResponseEntity.ok(transaksiService.filterMyTransactionsByStatus(status, page, size, sortBy, sortDir));
        } catch (Exception e) {
            log.error("Error saat filter transaksi: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal filter transaksi");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search transaksi user berdasarkan kode pembayaran dengan pagination")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TransaksiDTO>> searchMyTransactions(
            @RequestParam String kodePembayaran,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            log.info("User mencari transaksi dengan kode: {} - page: {}, size: {}", kodePembayaran, page, size);
            return ResponseEntity.ok(transaksiService.searchMyTransactions(kodePembayaran, page, size, sortBy, sortDir));
        } catch (Exception e) {
            log.error("Error saat mencari transaksi: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal mencari transaksi");
        }
    }
}
