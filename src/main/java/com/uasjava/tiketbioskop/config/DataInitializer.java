package com.uasjava.tiketbioskop.config;

import com.uasjava.tiketbioskop.model.*;
import com.uasjava.tiketbioskop.repository.*;
import com.uasjava.tiketbioskop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");

        // Create roles
        createRoles();

        // Create users
        createUsers();

        // Create bioskop
        createBioskop();

        // Create films
        createFilms();

        // Create jadwal
        createJadwal();

        // Create kursi
        createKursi();

        log.info("Sample data initialization completed!");
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
                    .createdDate(new Date())
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
                    .createdDate(new Date())
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
        if (bioskopRepository.findByNama("CGV Grand Indonesia") == null) {
            Bioskop bioskop1 = Bioskop.builder()
                    .nama("CGV Grand Indonesia")
                    .lokasi("Jakarta Pusat")
                    .build();
            bioskopRepository.save(bioskop1);
            log.info("Created bioskop: CGV Grand Indonesia");
        }

        if (bioskopRepository.findByNama("XXI Plaza Indonesia") == null) {
            Bioskop bioskop2 = Bioskop.builder()
                    .nama("XXI Plaza Indonesia")
                    .lokasi("Jakarta Selatan")
                    .build();
            bioskopRepository.save(bioskop2);
            log.info("Created bioskop: XXI Plaza Indonesia");
        }

        if (bioskopRepository.findByNama("Cinepolis Grand Mall") == null) {
            Bioskop bioskop3 = Bioskop.builder()
                    .nama("Cinepolis Grand Mall")
                    .lokasi("Bekasi")
                    .build();
            bioskopRepository.save(bioskop3);
            log.info("Created bioskop: Cinepolis Grand Mall");
        }
    }

    private void createFilms() {
        if (filmRepository.findByJudul("Avengers: Endgame") == null) {
            Film film1 = Film.builder()
                    .judul("Avengers: Endgame")
                    .genre("Action")
                    .durasi(181)
                    .sinopsis("After the devastating events of Avengers: Infinity War, the universe is in ruins.")
                    .cast("Robert Downey Jr., Chris Evans, Mark Ruffalo")
                    .trailerUrl("https://youtube.com/avengers")
                    .status(Film.StatusFilm.TAYANG)
                    .build();
            filmRepository.save(film1);
            log.info("Created film: Avengers: Endgame");
        }

        if (filmRepository.findByJudul("Spider-Man: No Way Home") == null) {
            Film film2 = Film.builder()
                    .judul("Spider-Man: No Way Home")
                    .genre("Action")
                    .durasi(148)
                    .sinopsis("Peter Parker seeks help from Doctor Strange to make people forget his identity.")
                    .cast("Tom Holland, Zendaya, Benedict Cumberbatch")
                    .trailerUrl("https://youtube.com/spiderman")
                    .status(Film.StatusFilm.TAYANG)
                    .build();
            filmRepository.save(film2);
            log.info("Created film: Spider-Man: No Way Home");
        }

        if (filmRepository.findByJudul("Black Panther") == null) {
            Film film3 = Film.builder()
                    .judul("Black Panther")
                    .genre("Action")
                    .durasi(134)
                    .sinopsis("T'Challa returns home to Wakanda to take his place as king.")
                    .cast("Chadwick Boseman, Michael B. Jordan, Lupita Nyong'o")
                    .trailerUrl("https://youtube.com/blackpanther")
                    .status(Film.StatusFilm.SEGERA_TAYANG)
                    .build();
            filmRepository.save(film3);
            log.info("Created film: Black Panther");
        }

        if (filmRepository.findByJudul("The Batman") == null) {
            Film film4 = Film.builder()
                    .judul("The Batman")
                    .genre("Action")
                    .durasi(176)
                    .sinopsis("Batman ventures into Gotham City's underworld.")
                    .cast("Robert Pattinson, Zoë Kravitz, Jeffrey Wright")
                    .trailerUrl("https://youtube.com/thebatman")
                    .status(Film.StatusFilm.TAYANG)
                    .build();
            filmRepository.save(film4);
            log.info("Created film: The Batman");
        }
    }

    private void createJadwal() {
        if (jadwalRepository.count() == 0) {
            List<Bioskop> bioskopList = bioskopRepository.findAll();
            List<Film> filmList = filmRepository.findAll();

            if (!bioskopList.isEmpty() && !filmList.isEmpty()) {
                Bioskop bioskop1 = bioskopList.get(0);
                Film film1 = filmList.get(0);
                Film film2 = filmList.get(1);

                Jadwal jadwal1 = Jadwal.builder()
                        .film(film1)
                        .bioskop(bioskop1)
                        .tanggal(LocalDate.now().plusDays(1))
                        .jam(LocalTime.of(10, 0))
                        .build();
                jadwalRepository.save(jadwal1);

                Jadwal jadwal2 = Jadwal.builder()
                        .film(film1)
                        .bioskop(bioskop1)
                        .tanggal(LocalDate.now().plusDays(1))
                        .jam(LocalTime.of(14, 0))
                        .build();
                jadwalRepository.save(jadwal2);

                Jadwal jadwal3 = Jadwal.builder()
                        .film(film2)
                        .bioskop(bioskop1)
                        .tanggal(LocalDate.now().plusDays(1))
                        .jam(LocalTime.of(18, 0))
                        .build();
                jadwalRepository.save(jadwal3);

                Jadwal jadwal4 = Jadwal.builder()
                        .film(film1)
                        .bioskop(bioskop1)
                        .tanggal(LocalDate.now().plusDays(2))
                        .jam(LocalTime.of(12, 0))
                        .build();
                jadwalRepository.save(jadwal4);

                log.info("Created 4 jadwal");
            }
        }
    }

    private void createKursi() {
        if (kursiRepository.count() == 0) {
            List<Bioskop> bioskopList = bioskopRepository.findAll();

            if (!bioskopList.isEmpty()) {
                Bioskop bioskop = bioskopList.get(0);

                // Create kursi satu per satu untuk menghindari error
                Kursi kursi1 = Kursi.builder().bioskop(bioskop).nomor("A1").tipe(Kursi.TipeKursi.REGULER).build();
                kursiRepository.save(kursi1);

                Kursi kursi2 = Kursi.builder().bioskop(bioskop).nomor("A2").tipe(Kursi.TipeKursi.REGULER).build();
                kursiRepository.save(kursi2);

                Kursi kursi3 = Kursi.builder().bioskop(bioskop).nomor("A3").tipe(Kursi.TipeKursi.REGULER).build();
                kursiRepository.save(kursi3);

                Kursi kursi4 = Kursi.builder().bioskop(bioskop).nomor("B1").tipe(Kursi.TipeKursi.REGULER).build();
                kursiRepository.save(kursi4);

                Kursi kursi5 = Kursi.builder().bioskop(bioskop).nomor("B2").tipe(Kursi.TipeKursi.REGULER).build();
                kursiRepository.save(kursi5);

                Kursi kursi6 = Kursi.builder().bioskop(bioskop).nomor("C1").tipe(Kursi.TipeKursi.VIP).build();
                kursiRepository.save(kursi6);

                Kursi kursi7 = Kursi.builder().bioskop(bioskop).nomor("C2").tipe(Kursi.TipeKursi.VIP).build();
                kursiRepository.save(kursi7);

                log.info("Created 7 kursi");
            }
        }
    }
}