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
// @RequestMapping("/api/kursi")
@RequiredArgsConstructor
@Slf4j
public class KursiController {
    private final KursiService kursiService;

    @PostMapping("/admin/kursi")
    @Operation(summary = "Menambah kursi baru")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<KursiDTO>> create(@Valid @RequestBody KursiDTO dto) {
        try {
            log.info("Admin menambah kursi baru: {} untuk bioskop ID: {}", dto.getNomor(), dto.getBioskopId());
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", kursiService.create(dto)));
        } catch (Exception e) {
            log.error("Error saat menambah kursi: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal menambah kursi baru");
        }
    }

    @GetMapping("/all/kursi")
    @Operation(summary = "Menampilkan semua kursi baru")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<Page<KursiDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Mengambil semua data kursi dengan pagination - page: {}, size: {}, sortBy: {}, sortDir: {}",
                     page, size, sortBy, sortDir);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                sortDir.equalsIgnoreCase("desc") ?
                    org.springframework.data.domain.Sort.by(sortBy).descending() :
                    org.springframework.data.domain.Sort.by(sortBy).ascending()
            );

            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", kursiService.getAll(pageable)));
        } catch (Exception e) {
            log.error("Error saat mengambil data kursi: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data kursi");
        }
    }

    @GetMapping("/all/kursi/{id}")
    @Operation(summary = "Menampilkan kursi berdasarkan id ")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<KursiDTO>> getById(@PathVariable Long id) {
        try {
            log.info("Mengambil kursi dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", kursiService.getById(id)));
        } catch (Exception e) {
            log.error("Error saat mengambil kursi dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data kursi");
        }
    }

    @PutMapping("/admin/kursi/{id}")
    @Operation(summary = "Mengubah kursi berdasarkan id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<KursiDTO>> update(@PathVariable Long id, @Valid @RequestBody KursiDTO dto) {
        try {
            log.info("Admin mengubah kursi dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", kursiService.update(id, dto)));
        } catch (Exception e) {
            log.error("Error saat mengubah kursi dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengubah kursi");
        }
    }

    @DeleteMapping("/admin/kursi/{id}")
    @Operation(summary = "Menghapus kursi berdasarkan id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        try {
            log.info("Admin menghapus kursi dengan ID: {}", id);
            kursiService.delete(id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
        } catch (Exception e) {
            log.error("Error saat menghapus kursi dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal menghapus kursi");
        }
    }
}

