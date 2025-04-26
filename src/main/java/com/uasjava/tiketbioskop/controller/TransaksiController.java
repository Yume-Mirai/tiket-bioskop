package com.uasjava.tiketbioskop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;
import com.uasjava.tiketbioskop.dto.KonfirmasiPembayaranDTO;
import com.uasjava.tiketbioskop.service.TransaksiService;

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
}
