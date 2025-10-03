package com.uasjava.tiketbioskop.exception;

public class KursiNotAvailableException extends RuntimeException {
    public KursiNotAvailableException(String message) {
        super(message);
    }

    public KursiNotAvailableException(String kursiNomor, String alasan) {
        super(String.format("Kursi %s tidak tersedia: %s", kursiNomor, alasan));
    }
}