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
// @RequestMapping("/api/jadwal")
@RequiredArgsConstructor
@Slf4j
public class JadwalController {
    private final JadwalService jadwalService;

    @PostMapping("/admin/jadwal")
    @Operation(summary = "Membuat jadwal baru")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<JadwalDTO>> create(@Valid @RequestBody JadwalDTO dto) {
        try {
            log.info("Admin membuat jadwal baru untuk film ID: {} dan bioskop ID: {}", dto.getFilmId(), dto.getBioskopId());
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", jadwalService.create(dto)));
        } catch (Exception e) {
            log.error("Error saat membuat jadwal: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal membuat jadwal baru");
        }
    }

    @GetMapping("/all/jadwal")
    @Operation(summary = "Menampilkan semua data jadwal")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<Page<JadwalDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Mengambil semua data jadwal dengan pagination - page: {}, size: {}, sortBy: {}, sortDir: {}",
                     page, size, sortBy, sortDir);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                sortDir.equalsIgnoreCase("desc") ?
                    org.springframework.data.domain.Sort.by(sortBy).descending() :
                    org.springframework.data.domain.Sort.by(sortBy).ascending()
            );

            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", jadwalService.getAll(pageable)));
        } catch (Exception e) {
            log.error("Error saat mengambil data jadwal: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data jadwal");
        }
    }

    @GetMapping("/all/jadwal/{id}")
    @Operation(summary = "Menampilkan data jadwal sesuai id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<JadwalDTO>> getById(@PathVariable Long id) {
        try {
            log.info("Mengambil jadwal dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", jadwalService.getById(id)));
        } catch (Exception e) {
            log.error("Error saat mengambil jadwal dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data jadwal");
        }
    }

    @PutMapping("/admin/jadwal/{id}")
    @Operation(summary = "Mengedit data jadwal sesuai id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<JadwalDTO>> update(@PathVariable Long id, @Valid @RequestBody JadwalDTO dto) {
        try {
            log.info("Admin mengupdate jadwal dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", jadwalService.update(id, dto)));
        } catch (Exception e) {
            log.error("Error saat update jadwal dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengupdate jadwal");
        }
    }

    @DeleteMapping("/admin/jadwal/{id}")
    @Operation(summary = "Menghapus data jadwal sesuai id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        try {
            log.info("Admin menghapus jadwal dengan ID: {}", id);
            jadwalService.delete(id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
        } catch (Exception e) {
            log.error("Error saat menghapus jadwal dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal menghapus jadwal");
        }
    }
}