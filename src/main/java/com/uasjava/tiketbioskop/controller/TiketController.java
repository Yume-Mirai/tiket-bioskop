package com.uasjava.tiketbioskop.controller;

import com.uasjava.tiketbioskop.dto.*;
import com.uasjava.tiketbioskop.service.*;
import com.uasjava.tiketbioskop.util.WebResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
// @RequestMapping("/api/tiket")
@RequiredArgsConstructor
@Slf4j
public class TiketController {
    private final TiketService tiketService;

    @PostMapping("/admin/tiket")
    @Operation(summary = "Menambah tiket baru")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<TiketDTO>> create(@Valid @RequestBody TiketDTO dto) {
        try {
            log.info("Admin menambah tiket baru untuk transaksi ID: {} dan kursi ID: {}", dto.getTransaksiId(), dto.getKursiId());
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", tiketService.create(dto)));
        } catch (Exception e) {
            log.error("Error saat menambah tiket: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal menambah tiket baru");
        }
    }

    @GetMapping("/all/tiket")
    @Operation(summary = "menampilkan semua data tiket ")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<Page<TiketDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Admin mengambil semua data tiket dengan pagination - page: {}, size: {}, sortBy: {}, sortDir: {}",
                     page, size, sortBy, sortDir);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                sortDir.equalsIgnoreCase("desc") ?
                    org.springframework.data.domain.Sort.by(sortBy).descending() :
                    org.springframework.data.domain.Sort.by(sortBy).ascending()
            );

            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", tiketService.getAll(pageable)));
        } catch (Exception e) {
            log.error("Error saat mengambil data tiket: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data tiket");
        }
    }

    @GetMapping("/all/tiket/{id}")
    @Operation(summary = "Menampilkan tiket berdasarkan id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<TiketDTO>> getById(@PathVariable Long id) {
        try {
            log.info("Admin mengambil tiket dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", tiketService.getById(id)));
        } catch (Exception e) {
            log.error("Error saat mengambil tiket dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data tiket");
        }
    }

    @PutMapping("/admin/tiket/{id}")
    @Operation(summary = "Mengubah tiket berdasarkan id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<TiketDTO>> update(@PathVariable Long id, @Valid @RequestBody TiketDTO dto) {
        try {
            log.info("Admin mengubah tiket dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", tiketService.update(id, dto)));
        } catch (Exception e) {
            log.error("Error saat mengubah tiket dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengubah tiket");
        }
    }

    @DeleteMapping("/admin/tiket/{id}")
    @Operation(summary = "Menghapus tiket berdasarkan id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        try {
            log.info("Admin menghapus tiket dengan ID: {}", id);
            tiketService.delete(id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
        } catch (Exception e) {
            log.error("Error saat menghapus tiket dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal menghapus tiket");
        }
    }
}