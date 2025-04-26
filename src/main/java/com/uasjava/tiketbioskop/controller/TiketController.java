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
// @RequestMapping("/api/tiket")
@RequiredArgsConstructor
public class TiketController {
    private final TiketService tiketService;

    @PostMapping("/admin/tiket")
    public ResponseEntity<WebResponse<TiketDTO>> create(@Valid @RequestBody TiketDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", tiketService.create(dto)));
    }

    @GetMapping("/all/tiket")
    public ResponseEntity<WebResponse<Page<TiketDTO>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", tiketService.getAll(pageable)));
    }

    @GetMapping("/all/tiket/{id}")
    public ResponseEntity<WebResponse<TiketDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", tiketService.getById(id)));
    }

    @PutMapping("/admin/tiket/{id}")
    public ResponseEntity<WebResponse<TiketDTO>> update(@PathVariable Long id, @Valid @RequestBody TiketDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", tiketService.update(id, dto)));
    }

    @DeleteMapping("/admin/tiket/{id}")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        tiketService.delete(id);
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
    }
}