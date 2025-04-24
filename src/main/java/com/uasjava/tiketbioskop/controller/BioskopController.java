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
@RequestMapping("/api/bioskop")
@RequiredArgsConstructor
public class BioskopController {
    private final BioskopService bioskopService;

    @PostMapping
    public ResponseEntity<WebResponse<BioskopDTO>> create(@Valid @RequestBody BioskopDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil tambah", bioskopService.create(dto)));
    }

    @GetMapping
    public ResponseEntity<WebResponse<Page<BioskopDTO>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", bioskopService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<BioskopDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil ambil data", bioskopService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<BioskopDTO>> update(@PathVariable Long id, @Valid @RequestBody BioskopDTO dto) {
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil update", bioskopService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<String>> delete(@PathVariable Long id) {
        bioskopService.delete(id);
        return ResponseEntity.ok(new WebResponse<>(200, "Berhasil hapus", "OK"));
    }
}
