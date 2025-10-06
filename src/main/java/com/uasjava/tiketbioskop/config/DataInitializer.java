package com.uasjava.tiketbioskop.config;

import com.uasjava.tiketbioskop.model.*;
import com.uasjava.tiketbioskop.repository.*;
import com.uasjava.tiketbioskop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final BioskopRepository bioskopRepository;
    private final FilmRepository filmRepository;
    private final JadwalRepository jadwalRepository;
    private final KursiRepository kursiRepository;

    /**
     * Method untuk mencari data bioskop yang belum dibuat dengan kondisi spesifik
     */
    private List<String> findMissingBioskopData() {
        List<String> missingData = new ArrayList<>();

        // Data bioskop yang akan dicek
        String[][] bioskopData = {
            {"CGV Grand Indonesia", "Jakarta Pusat"},
            {"XXI Plaza Indonesia", "Jakarta Selatan"},
            {"Cinepolis Grand Mall", "Bekasi"}
        };

        for (String[] data : bioskopData) {
            String nama = data[0];
            String lokasi = data[1];
            if (bioskopRepository.findByNamaAndLokasi(nama, lokasi) == null) {
                missingData.add("Bioskop: " + nama + " - " + lokasi);
            }
        }

        return missingData;
    }

    /**
     * Method untuk mencari data film yang belum dibuat dengan kondisi spesifik
     */
    private List<String> findMissingFilmData() {
        List<String> missingData = new ArrayList<>();

        // Data film yang akan dicek (judul, genre, durasi, sinopsis, cast, trailer, status)
        Object[][] filmData = {
            {"Avengers: Endgame", "Action", 181,
             "After the devastating events of Avengers: Infinity War, the universe is in ruins due to the snap that wiped out half of all life.",
             "Robert Downey Jr., Chris Evans, Mark Ruffalo, Chris Hemsworth, Scarlett Johansson",
             "https://youtube.com/avengers-endgame", Film.StatusFilm.TAYANG},

            {"Spider-Man: No Way Home", "Action", 148,
             "With Spider-Man's identity now revealed, Peter Parker asks Doctor Strange for help to restore his secret.",
             "Tom Holland, Zendaya, Benedict Cumberbatch, Jacob Batalon, Jon Favreau",
             "https://youtube.com/spiderman-nowayhome", Film.StatusFilm.TAYANG},

            {"Black Panther", "Action", 134,
             "T'Challa, heir to the hidden but advanced kingdom of Wakanda, must step forward to lead his people.",
             "Chadwick Boseman, Michael B. Jordan, Lupita Nyong'o, Danai Gurira, Martin Freeman",
             "https://youtube.com/blackpanther", Film.StatusFilm.SEGERA_TAYANG},

            {"The Batman", "Action", 176,
             "When a killer targets Gotham's elite with a series of sadistic machinations, a trail of cryptic clues sends the World's Greatest Detective on an investigation.",
             "Robert Pattinson, Zoë Kravitz, Jeffrey Wright, Colin Farrell, Paul Dano",
             "https://youtube.com/thebatman", Film.StatusFilm.TAYANG}
        };

        for (Object[] data : filmData) {
            String judul = (String) data[0];
            String genre = (String) data[1];
            if (filmRepository.findByJudulAndGenre(judul, genre) == null) {
                missingData.add("Film: " + judul + " (" + genre + ")");
            }
        }

        return missingData;
    }

    /**
     * Method untuk mencari data jadwal yang belum dibuat dengan kondisi count dan dependency check
     */
    private List<String> findMissingJadwalData() {
        List<String> missingData = new ArrayList<>();

        // Pastikan ada bioskop dan film sebelum cek jadwal
        List<Bioskop> bioskopList = bioskopRepository.findAll();
        List<Film> filmList = filmRepository.findAll();

        if (bioskopList.isEmpty() || filmList.isEmpty()) {
            missingData.add("Dependency Error: Tidak ada bioskop atau film untuk membuat jadwal");
            return missingData;
        }

        Bioskop bioskop = bioskopList.get(0);
        Film film1 = filmList.get(0);
        Film film2 = filmList.size() > 1 ? filmList.get(1) : film1;

        // Cek jadwal spesifik yang akan dibuat
        LocalDate[] tanggalList = {LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)};
        LocalTime[] jamList = {LocalTime.of(10, 0), LocalTime.of(14, 0), LocalTime.of(18, 0), LocalTime.of(12, 0)};

        int jadwalIndex = 0;
        for (int i = 0; i < tanggalList.length && jadwalIndex < jamList.length; i++) {
            for (int j = 0; j < Math.min(2, jamList.length - jadwalIndex); j++) {
                LocalDate tanggal = tanggalList[i];
                LocalTime jam = jamList[jadwalIndex];

                Film film = (jadwalIndex == 2) ? film2 : film1;
                jadwalIndex++;

                if (jadwalRepository.findByFilmIdAndBioskopIdAndTanggalAndJam(
                        film.getId(), bioskop.getId(), tanggal, jam).isEmpty()) {
                    missingData.add("Jadwal: " + film.getJudul() + " - " + bioskop.getNama() +
                                  " pada " + tanggal + " jam " + jam);
                }
            }
        }

        return missingData;
    }

    /**
     * Method untuk mencari data kursi yang belum dibuat dengan kondisi count dan dependency check
     */
    private List<String> findMissingKursiData() {
        List<String> missingData = new ArrayList<>();

        // Pastikan ada bioskop sebelum cek kursi
        List<Bioskop> bioskopList = bioskopRepository.findAll();
        if (bioskopList.isEmpty()) {
            missingData.add("Dependency Error: Tidak ada bioskop untuk membuat kursi");
            return missingData;
        }

        Bioskop bioskop = bioskopList.get(0);

        // Kursi yang akan dicek
        String[][] kursiData = {
            {"A1", "REGULER"},
            {"A2", "REGULER"},
            {"A3", "REGULER"},
            {"B1", "REGULER"},
            {"B2", "REGULER"},
            {"C1", "VIP"},
            {"C2", "VIP"}
        };

        for (String[] data : kursiData) {
            String nomor = data[0];
            String tipeStr = data[1];
            Kursi.TipeKursi tipe = Kursi.TipeKursi.valueOf(tipeStr);

            if (kursiRepository.findByBioskopIdAndNomor(bioskop.getId(), nomor).isEmpty()) {
                missingData.add("Kursi: " + nomor + " (" + tipe + ") di " + bioskop.getNama());
            }
        }

        return missingData;
    }

    /**
     * Method untuk menampilkan summary data yang akan dibuat
     */
    private void logMissingDataSummary() {
        log.info("=== ANALISIS DATA YANG BELUM DIBUAT ===");

        List<String> missingBioskop = findMissingBioskopData();
        List<String> missingFilm = findMissingFilmData();
        List<String> missingJadwal = findMissingJadwalData();
        List<String> missingKursi = findMissingKursiData();

        if (!missingBioskop.isEmpty()) {
            log.info("Bioskop yang akan dibuat:");
            missingBioskop.forEach(log::info);
        }

        if (!missingFilm.isEmpty()) {
            log.info("Film yang akan dibuat:");
            missingFilm.forEach(log::info);
        }

        if (!missingJadwal.isEmpty()) {
            log.info("Jadwal yang akan dibuat:");
            missingJadwal.forEach(log::info);
        }

        if (!missingKursi.isEmpty()) {
            log.info("Kursi yang akan dibuat:");
            missingKursi.forEach(log::info);
        }

        if (missingBioskop.isEmpty() && missingFilm.isEmpty() &&
            missingJadwal.isEmpty() && missingKursi.isEmpty()) {
            log.info("Semua data sudah lengkap, tidak ada yang perlu dibuat");
        }

        log.info("=== AKHIR ANALISIS ===");
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");

        try {
            // Create roles first (independent entities)
            createRoles();

            // Create users (depends on roles)
            createUsers();

            // Create bioskop (independent)
            createBioskop();

            // Create films (independent)
            createFilms();

            // Create jadwal (depends on bioskop and film)
            createJadwal();

            // Create kursi (depends on bioskop)
            createKursi();

            log.info("Sample data initialization completed successfully!");
        } catch (Exception e) {
            log.error("Error initializing sample data: ", e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }

    private void createRoles() {
        // Check if roles already exist to avoid conflicts
        if (roleRepository.findById(1).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setId(1);
            adminRole.setRoleName("ADMIN");
            adminRole.setCreatedDate(LocalDate.now());
            roleRepository.save(adminRole);
            log.info("Created role: ADMIN");
        } else {
            log.info("Role ADMIN already exists, skipping creation");
        }

        if (roleRepository.findById(2).isEmpty()) {
            Role userRole = new Role();
            userRole.setId(2);
            userRole.setRoleName("USER");
            userRole.setCreatedDate(LocalDate.now());
            roleRepository.save(userRole);
            log.info("Created role: USER");
        } else {
            log.info("Role USER already exists, skipping creation");
        }
    }

    private void createUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Create admin user
            Users adminUser = Users.builder()
                    .username("admin")
                    .password(PasswordUtil.hash("admin"))
                    .email("admin@bioskop.com")
                    .nomor("081234567890")
                    .tanggal_lahir(LocalDate.of(1990, 1, 1))
                    .status(true)
                    .build();

            userRepository.save(adminUser);

            // Assign admin role
            Role adminRole = roleRepository.findById(1).orElse(null);
            if (adminRole != null) {
                UserRole adminUserRole = UserRole.builder()
                        .users(adminUser)
                        .role(adminRole)
                        .build();
                userRoleRepository.save(adminUserRole);
                log.info("Created admin user: admin/admin");
            } else {
                log.info("Admin user created but role not found");
            }
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            // Create regular user
            Users regularUser = Users.builder()
                    .username("user")
                    .password(PasswordUtil.hash("user"))
                    .email("user@bioskop.com")
                    .nomor("081234567891")
                    .tanggal_lahir(LocalDate.of(1995, 5, 15))
                    .status(true)
                    .build();

            userRepository.save(regularUser);

            // Assign user role
            Role userRole = roleRepository.findById(2).orElse(null);
            if (userRole != null) {
                UserRole userUserRole = UserRole.builder()
                        .users(regularUser)
                        .role(userRole)
                        .build();
                userRoleRepository.save(userUserRole);
                log.info("Created regular user: user/user");
            } else {
                log.info("Regular user created but role not found");
            }
        }
    }

    private void createBioskop() {
        // Data bioskop yang akan dibuat
        String[][] bioskopData = {
            {"CGV Grand Indonesia", "Jakarta Pusat"},
            {"XXI Plaza Indonesia", "Jakarta Selatan"},
            {"Cinepolis Grand Mall", "Bekasi"}
        };

        for (String[] data : bioskopData) {
            String nama = data[0];
            String lokasi = data[1];

            try {
                // Cek apakah bioskop sudah ada
                Optional<Bioskop> existingBioskop = bioskopRepository.findByNamaAndLokasi(nama, lokasi);
                if (existingBioskop.isEmpty()) {
                    // Buat bioskop baru jika belum ada
                    Bioskop bioskop = Bioskop.builder()
                            .nama(nama)
                            .lokasi(lokasi)
                            .build();

                    bioskopRepository.save(bioskop);
                    log.info("Created bioskop: {} di {} dengan ID: {}", nama, lokasi, bioskop.getId());
                } else {
                    log.info("Bioskop {} di {} already exists, skipping creation", nama, lokasi);
                }
            } catch (Exception e) {
                log.error("Error creating bioskop {} di {}: {}", nama, lokasi, e.getMessage());
            }
        }
    }

    private void createFilms() {
        // Data film yang akan dibuat (judul, genre, durasi, sinopsis, cast, trailer, status)
        Object[][] filmData = {
            {"Avengers: Endgame", "Action", 181,
             "After the devastating events of Avengers: Infinity War, the universe is in ruins due to the snap that wiped out half of all life.",
             "Robert Downey Jr., Chris Evans, Mark Ruffalo, Chris Hemsworth, Scarlett Johansson",
             "https://youtube.com/avengers-endgame", Film.StatusFilm.TAYANG},

            {"Spider-Man: No Way Home", "Action", 148,
             "With Spider-Man's identity now revealed, Peter Parker asks Doctor Strange for help to restore his secret.",
             "Tom Holland, Zendaya, Benedict Cumberbatch, Jacob Batalon, Jon Favreau",
             "https://youtube.com/spiderman-nowayhome", Film.StatusFilm.TAYANG},

            {"Black Panther", "Action", 134,
             "T'Challa, heir to the hidden but advanced kingdom of Wakanda, must step forward to lead his people.",
             "Chadwick Boseman, Michael B. Jordan, Lupita Nyong'o, Danai Gurira, Martin Freeman",
             "https://youtube.com/blackpanther", Film.StatusFilm.SEGERA_TAYANG},

            {"The Batman", "Action", 176,
             "When a killer targets Gotham's elite with a series of sadistic machinations, a trail of cryptic clues sends the World's Greatest Detective on an investigation.",
             "Robert Pattinson, Zoë Kravitz, Jeffrey Wright, Colin Farrell, Paul Dano",
             "https://youtube.com/thebatman", Film.StatusFilm.TAYANG}
        };

        for (Object[] data : filmData) {
            String judul = (String) data[0];
            String genre = (String) data[1];

            try {
                // Cek apakah film sudah ada
                Film existingFilm = filmRepository.findByJudulAndGenre(judul, genre);
                if (existingFilm == null) {
                    // Buat film baru jika belum ada
                    Film film = Film.builder()
                            .judul(judul)
                            .genre(genre)
                            .durasi((Integer) data[2])
                            .sinopsis((String) data[3])
                            .cast((String) data[4])
                            .trailerUrl((String) data[5])
                            .status((Film.StatusFilm) data[6])
                            .build();

                    filmRepository.save(film);
                    log.info("Created film: {} ({}) dengan ID: {}", judul, genre, film.getId());
                } else {
                    log.info("Film {} ({}) already exists, skipping creation", judul, genre);
                }
            } catch (Exception e) {
                log.error("Error creating film {} ({}): {}", judul, genre, e.getMessage());
            }
        }
    }

    private void createJadwal() {
        // Pastikan ada bioskop dan film sebelum membuat jadwal
        List<Bioskop> bioskopList = bioskopRepository.findAll();
        List<Film> filmList = filmRepository.findAll();

        log.info("Found {} bioskop and {} films for jadwal creation", bioskopList.size(), filmList.size());

        if (bioskopList.isEmpty() || filmList.isEmpty()) {
            log.warn("Cannot create jadwal: Bioskop or Film data is empty");
            return;
        }

        Bioskop bioskop1 = bioskopList.get(0);
        Film film1 = filmList.get(0);
        Film film2 = filmList.size() > 1 ? filmList.get(1) : film1;

        log.info("Creating jadwal for bioskop: {} and films: {}, {}",
                bioskop1.getNama(), film1.getJudul(), film2.getJudul());

        // Create jadwal dengan kondisi pengecekan yang spesifik
        createJadwalIfNotExists(film1, bioskop1, LocalDate.now().plusDays(1), LocalTime.of(10, 0), "Jadwal 1");
        createJadwalIfNotExists(film1, bioskop1, LocalDate.now().plusDays(1), LocalTime.of(14, 0), "Jadwal 2");
        createJadwalIfNotExists(film2, bioskop1, LocalDate.now().plusDays(1), LocalTime.of(18, 0), "Jadwal 3");
        createJadwalIfNotExists(film1, bioskop1, LocalDate.now().plusDays(2), LocalTime.of(12, 0), "Jadwal 4");
    }

    private void createJadwalIfNotExists(Film film, Bioskop bioskop, LocalDate tanggal, LocalTime jam, String jadwalName) {
        // Cek dengan kondisi yang sangat spesifik
        if (jadwalRepository.findByFilmIdAndBioskopIdAndTanggalAndJam(film.getId(), bioskop.getId(), tanggal, jam).isEmpty()) {
            Jadwal jadwal = Jadwal.builder()
                    .film(film)
                    .bioskop(bioskop)
                    .tanggal(tanggal)
                    .jam(jam)
                    .build();

            try {
                jadwalRepository.save(jadwal);
                log.info("Created {}: {} - {} pada {} jam {}", jadwalName, film.getJudul(), bioskop.getNama(), tanggal, jam);
            } catch (Exception e) {
                log.error("Error creating {}: {}", jadwalName, e.getMessage());
            }
        } else {
            log.info("{} already exists, skipping creation", jadwalName);
        }
    }

    private void createKursi() {
        List<Bioskop> bioskopList = bioskopRepository.findAll();

        log.info("Found {} bioskop for kursi creation", bioskopList.size());

        if (bioskopList.isEmpty()) {
            log.warn("Cannot create kursi: No bioskop found");
            return;
        }

        Bioskop bioskop = bioskopList.get(0);
        log.info("Creating kursi for bioskop: {}", bioskop.getNama());

        // Create kursi dengan kondisi pengecekan yang spesifik
        createKursiIfNotExists(bioskop, "A1", Kursi.TipeKursi.REGULER, "Kursi A1");
        createKursiIfNotExists(bioskop, "A2", Kursi.TipeKursi.REGULER, "Kursi A2");
        createKursiIfNotExists(bioskop, "A3", Kursi.TipeKursi.REGULER, "Kursi A3");
        createKursiIfNotExists(bioskop, "B1", Kursi.TipeKursi.REGULER, "Kursi B1");
        createKursiIfNotExists(bioskop, "B2", Kursi.TipeKursi.REGULER, "Kursi B2");
        createKursiIfNotExists(bioskop, "C1", Kursi.TipeKursi.VIP, "Kursi C1 VIP");
        createKursiIfNotExists(bioskop, "C2", Kursi.TipeKursi.VIP, "Kursi C2 VIP");
    }

    private void createKursiIfNotExists(Bioskop bioskop, String nomor, Kursi.TipeKursi tipe, String kursiName) {
        // Cek dengan kondisi yang sangat spesifik
        if (kursiRepository.findByBioskopIdAndNomor(bioskop.getId(), nomor).isEmpty()) {
            Kursi kursi = Kursi.builder()
                    .bioskop(bioskop)
                    .nomor(nomor)
                    .tipe(tipe)
                    .build();

            try {
                kursiRepository.save(kursi);
                log.info("Created {}: {} - {} ({})", kursiName, bioskop.getNama(), nomor, tipe);
            } catch (Exception e) {
                log.error("Error creating {}: {}", kursiName, e.getMessage());
            }
        } else {
            log.info("{} already exists, skipping creation", kursiName);
        }
    }
}