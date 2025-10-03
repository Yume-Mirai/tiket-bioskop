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
// @RequestMapping("/api/bioskop")
@RequiredArgsConstructor
@Slf4j
public class BioskopController {
    private final BioskopService bioskopService;

    @PostMapping("/admin/bioskop")
    @Operation(summary = "Membuat bioskop baru")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<BioskopDTO>> create(@Valid @RequestBody BioskopDTO dto) {
        try {
            log.info("Admin membuat bioskop baru: {}", dto.getNama());
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", bioskopService.create(dto)));
        } catch (Exception e) {
            log.error("Error saat membuat bioskop: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal membuat bioskop baru");
        }
    }

    @GetMapping("/all/bioskop")
    @Operation(summary = "Menampilkan data bioskop dengan pagination and sort")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<Page<BioskopDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Mengambil semua data bioskop dengan pagination - page: {}, size: {}, sortBy: {}, sortDir: {}",
                     page, size, sortBy, sortDir);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size,
                sortDir.equalsIgnoreCase("desc") ?
                    org.springframework.data.domain.Sort.by(sortBy).descending() :
                    org.springframework.data.domain.Sort.by(sortBy).ascending()
            );

            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", bioskopService.getAll(pageable)));
        } catch (Exception e) {
            log.error("Error saat mengambil data bioskop: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data bioskop");
        }
    }

    @GetMapping("/all/bioskop/search")
    @Operation(summary = "Mencari bioskop berdasarkan nama dengan pagination dan sorting")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<Page<BioskopDTO>>> searchBioskop(
            @RequestParam String nama,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "nama") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Mencari bioskop dengan nama: {} - page: {}, size: {}", nama, page, size);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil cari data",
                bioskopService.searchBioskop(nama, page, size, sortBy, sortDir)));
        } catch (Exception e) {
            log.error("Error saat mencari bioskop: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal mencari bioskop");
        }
    }

    @GetMapping("/all/bioskop/filter")
    @Operation(summary = "Filter bioskop berdasarkan lokasi dengan pagination dan sorting")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<Page<BioskopDTO>>> filterBioskopByLocation(
            @RequestParam String lokasi,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10000") int size,
            @RequestParam(defaultValue = "nama") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Filter bioskop berdasarkan lokasi: {} - page: {}, size: {}", lokasi, page, size);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil filter data",
                bioskopService.filterBioskopByLocation(lokasi, page, size, sortBy, sortDir)));
        } catch (Exception e) {
            log.error("Error saat filter bioskop: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal filter bioskop");
        }
    }

    @GetMapping("/all/bioskop/{id}")
    @Operation(summary = "Menampilkan data bioskop sesuai id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WebResponse<BioskopDTO>> getById(@PathVariable Long id) {
        try {
            log.info("Mengambil bioskop dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", bioskopService.getById(id)));
        } catch (Exception e) {
            log.error("Error saat mengambil bioskop dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengambil data bioskop");
        }
    }

    @PutMapping("/admin/bioskop/{id}")
    @Operation(summary = "Mengedit data bioskop sesuai id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<BioskopDTO>> update(@PathVariable Long id, @Valid @RequestBody BioskopDTO dto) {
        try {
            log.info("Admin mengupdate bioskop dengan ID: {}", id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", bioskopService.update(id, dto)));
        } catch (Exception e) {
            log.error("Error saat update bioskop dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal mengupdate bioskop");
        }
    }

    @DeleteMapping("/admin/bioskop/{id}")
    @Operation(summary = "Menghapus data bioskop sesuai id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        try {
            log.info("Admin menghapus bioskop dengan ID: {}", id);
            bioskopService.delete(id);
            return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
        } catch (Exception e) {
            log.error("Error saat menghapus bioskop dengan ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Gagal menghapus bioskop");
        }
    }
}
