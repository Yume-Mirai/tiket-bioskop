package com.uasjava.tiketbioskop.service;

import com.uasjava.tiketbioskop.dto.CheckoutRequestDTO;
import com.uasjava.tiketbioskop.dto.CheckoutResponseDTO;
import com.uasjava.tiketbioskop.dto.TransaksiDTO;
import org.springframework.data.domain.Page;

public interface TransaksiService {
    CheckoutResponseDTO checkout(CheckoutRequestDTO requesth);
    boolean konfirmasiPembayaran(String kodePembayaran);
    Page<TransaksiDTO> getMyTransactions(int page, int size, String sortBy, String sortDir);
    Page<TransaksiDTO> filterMyTransactionsByStatus(String status, int page, int size, String sortBy, String sortDir);
    Page<TransaksiDTO> searchMyTransactions(String kodePembayaran, int page, int size, String sortBy, String sortDir);
}
