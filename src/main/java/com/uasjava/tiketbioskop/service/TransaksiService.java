package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;

public interface TransaksiService {
    CheckoutResponseDTO checkout(CheckoutRequestDTO requesth);
    boolean konfirmasiPembayaran(String kodePembayaran);
}
