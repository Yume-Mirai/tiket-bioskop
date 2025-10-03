package com.uasjava.tiketbioskop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uasjava.tiketbioskop.model.Bioskop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BioskopRepository extends JpaRepository<Bioskop, Long> {

    // Cari bioskop berdasarkan nama (exact match)
    Optional<Bioskop> findByNama(String nama);

    // Cari bioskop berdasarkan nama (case insensitive, partial match)
    Page<Bioskop> findByNamaContainingIgnoreCase(String nama, Pageable pageable);

    // Cari bioskop berdasarkan lokasi (case insensitive, partial match)
    Page<Bioskop> findByLokasiContainingIgnoreCase(String lokasi, Pageable pageable);

    // Check apakah nama bioskop sudah digunakan (untuk validasi unik)
    boolean existsByNamaIgnoreCase(String nama);

    // Cari bioskop yang memiliki jadwal aktif
    @Query("SELECT DISTINCT b FROM Bioskop b JOIN b.jadwalList j WHERE j.tanggal >= CURRENT_DATE")
    List<Bioskop> findBioskopWithActiveSchedules();

    // Cari bioskop berdasarkan nama atau lokasi
    @Query("SELECT b FROM Bioskop b WHERE LOWER(b.nama) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.lokasi) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Bioskop> findByNamaOrLokasi(@Param("keyword") String keyword, Pageable pageable);

    // Hitung jumlah kursi di setiap bioskop
    @Query("SELECT b.nama, COUNT(k) FROM Bioskop b LEFT JOIN b.jadwalList j LEFT JOIN Kursi k ON k.bioskop = b GROUP BY b.id, b.nama")
    List<Object[]> countKursiByBioskop();
}
