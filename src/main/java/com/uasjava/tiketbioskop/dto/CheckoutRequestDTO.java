package com.uasjava.tiketbioskop.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequestDTO {

    @NotNull(message = "Jadwal ID tidak boleh kosong")
    private Long jadwalId;

    @NotEmpty(message = "Daftar kursi tidak boleh kosong")
    @Size(min = 1, max = 10, message = "Maksimal 10 kursi per transaksi")
    private List<Long> kursiIdList;

    @NotBlank(message = "Metode pembayaran tidak boleh kosong")
    @Pattern(regexp = "^(CASH|CREDIT_CARD|BANK_TRANSFER|E_WALLET)$",
             message = "Metode pembayaran harus CASH, CREDIT_CARD, BANK_TRANSFER, atau E_WALLET")
    private String metodePembayaran;
}
