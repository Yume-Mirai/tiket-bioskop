package com.uasjava.tiketbioskop.controller;
import com.uasjava.tiketbioskop.dto.*;
import com.uasjava.tiketbioskop.service.*;
import com.uasjava.tiketbioskop.util.WebResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
// @RequestMapping("/api/bioskop")
@RequiredArgsConstructor
public class BioskopController {
    private final BioskopService bioskopService;

    @PostMapping("/admin/bioskop")
    @Operation(summary = "Membuat bioskop baru")
    public ResponseEntity<WebResponse<BioskopDTO>> create(@Valid @RequestBody BioskopDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", bioskopService.create(dto)));
    }

    @GetMapping("/all/bioskop")
    @Operation(summary = "Menampilkan data bioskop")
    public ResponseEntity<WebResponse<Page<BioskopDTO>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", bioskopService.getAll(pageable)));
    }

    @GetMapping("/all/bioskop/{id}")
    @Operation(summary = "Menampilkan data bioskop sesuai id")
    public ResponseEntity<WebResponse<BioskopDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", bioskopService.getById(id)));
    }

    @PutMapping("/admin/bioskop/{id}")
    @Operation(summary = "Mengedit data bioskop sesuai id")
    public ResponseEntity<WebResponse<BioskopDTO>> update(@PathVariable Long id, @Valid @RequestBody BioskopDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", bioskopService.update(id, dto)));
    }

    @DeleteMapping("/admin/bioskop/{id}")
    @Operation(summary = "Menghapus data bioskop sesuai id")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        bioskopService.delete(id);
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
    }
}
