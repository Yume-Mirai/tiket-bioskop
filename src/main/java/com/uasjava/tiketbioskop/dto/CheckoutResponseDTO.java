package com.uasjava.tiketbioskop.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CheckoutResponseDTO {
    private Long transaksiId;
    private String kodePembayaran;
    private LocalDateTime expiredAt;
    private int totalHarga;
    private String metodePembayaran;
    private List<Long> tiketIdList;
}
