package com.uasjava.tiketbioskop.dto;

import lombok.Data;

@Data
public class TiketDTO {
    private Long id;
    private Long transaksiId;
    private Long kursiId;
    private int harga;
}
