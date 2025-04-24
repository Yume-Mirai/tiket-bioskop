package com.uasjava.tiketbioskop.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequestDTO {
    private Long jadwalId;
    private List<Long> kursiIdList;
    private String metodePembayaran;
}
