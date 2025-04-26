package com.uasjava.tiketbioskop.controller;

import com.uasjava.tiketbioskop.dto.*;
import com.uasjava.tiketbioskop.service.*;
import com.uasjava.tiketbioskop.util.WebResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// @RequestMapping("/api/jadwal")
@RequiredArgsConstructor
public class JadwalController {
    private final JadwalService jadwalService;

    @PostMapping("/admin/jadwal")
    public ResponseEntity<WebResponse<JadwalDTO>> create(@Valid @RequestBody JadwalDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", jadwalService.create(dto)));
    }

    @GetMapping("/all/jadwal")
    public ResponseEntity<WebResponse<Page<JadwalDTO>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", jadwalService.getAll(pageable)));
    }

    @GetMapping("/all/jadwal/{id}")
    public ResponseEntity<WebResponse<JadwalDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", jadwalService.getById(id)));
    }

    @PutMapping("/admin/jadwal/{id}")
    public ResponseEntity<WebResponse<JadwalDTO>> update(@PathVariable Long id, @Valid @RequestBody JadwalDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", jadwalService.update(id, dto)));
    }

    @DeleteMapping("/admin/jadwal/{id}")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        jadwalService.delete(id);
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
    }
}