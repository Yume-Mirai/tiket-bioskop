package com.uasjava.tiketbioskop.repository;

import com.uasjava.tiketbioskop.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface JadwalRepository extends JpaRepository<Jadwal, Long> {

    // Cari jadwal berdasarkan film
    List<Jadwal> findByFilm(Film film);

    // Cari jadwal berdasarkan bioskop
    List<Jadwal> findByBioskop(Bioskop bioskop);

    // Cari jadwal berdasarkan tanggal
    List<Jadwal> findByTanggal(LocalDate tanggal);

    // Cari jadwal berdasarkan film dan tanggal
    List<Jadwal> findByFilmAndTanggal(Film film, LocalDate tanggal);

    // Cari jadwal berdasarkan bioskop dan tanggal
    List<Jadwal> findByBioskopAndTanggal(Bioskop bioskop, LocalDate tanggal);


    // Cari jadwal berdasarkan rentang tanggal
    @Query("SELECT j FROM Jadwal j WHERE j.tanggal BETWEEN :startDate AND :endDate")
    List<Jadwal> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Cari jadwal yang tersedia (belum memiliki transaksi pending/lunas)
    @Query("SELECT j FROM Jadwal j WHERE j.id NOT IN " +
           "(SELECT t.jadwal.id FROM Transaksi t WHERE t.jadwal = j AND t.status IN ('PENDING', 'LUNAS'))")
    List<Jadwal> findAvailableJadwal();

    // Check apakah jadwal konflik dengan waktu yang sama di bioskop yang sama
    @Query("SELECT COUNT(j) > 0 FROM Jadwal j WHERE j.bioskop = :bioskop AND j.tanggal = :tanggal AND j.jam = :jam AND j.id != :jadwalId")
    boolean existsConflictingSchedule(@Param("bioskop") Bioskop bioskop, @Param("tanggal") LocalDate tanggal,
                                     @Param("jam") java.time.LocalTime jam, @Param("jadwalId") Long jadwalId);
}