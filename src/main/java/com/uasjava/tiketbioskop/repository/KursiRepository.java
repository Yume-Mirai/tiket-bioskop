package com.uasjava.tiketbioskop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uasjava.tiketbioskop.model.Bioskop;
import com.uasjava.tiketbioskop.model.Kursi;

import java.util.List;

public interface KursiRepository extends JpaRepository<Kursi, Long> {
    List<Kursi> findByBioskop(Bioskop bioskop);

    // Check kursi availability untuk jadwal tertentu
    @Query("SELECT k FROM Kursi k WHERE k.id IN :kursiIds " +
           "AND k.bioskop.id = :bioskopId " +
           "AND NOT EXISTS (SELECT t FROM Tiket t WHERE t.kursi = k " +
           "AND t.transaksi.jadwal.id = :jadwalId " +
           "AND t.transaksi.status = 'LUNAS')")
    List<Kursi> findAvailableKursiForJadwal(@Param("kursiIds") List<Long> kursiIds,
                                           @Param("bioskopId") Long bioskopId,
                                           @Param("jadwalId") Long jadwalId);

    // Check kursi yang sedang pending untuk mencegah double booking
    @Query("SELECT k FROM Kursi k WHERE k.id IN :kursiIds " +
           "AND EXISTS (SELECT t FROM Tiket t WHERE t.kursi = k " +
           "AND t.transaksi.jadwal.id = :jadwalId " +
           "AND t.transaksi.status = 'PENDING')")
    List<Kursi> findPendingKursiForJadwal(@Param("kursiIds") List<Long> kursiIds,
                                         @Param("jadwalId") Long jadwalId);

    // Cari kursi berdasarkan bioskop ID dan nomor kursi
    @Query("SELECT k FROM Kursi k WHERE k.bioskop.id = :bioskopId AND k.nomor = :nomor")
    List<Kursi> findByBioskopIdAndNomor(@Param("bioskopId") Long bioskopId, @Param("nomor") String nomor);
}
