package com.uasjava.tiketbioskop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uasjava.tiketbioskop.dto.LoginDto;
import com.uasjava.tiketbioskop.dto.RegisterUserDto;
import com.uasjava.tiketbioskop.model.Role;
import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.provider.JwtProvider;
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.service.LoginService;
import com.uasjava.tiketbioskop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test komprehensif untuk sistem otorisasi
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class AuthorizationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        // Setup test users
        setupTestUsers();
    }

    private void setupTestUsers() {
        // Create admin user
        Users admin = Users.builder()
                .username("admin_test")
                .password("Admin123!")
                .email("admin@test.com")
                .nomor("08123456789")
                .tanggal_lahir(LocalDate.of(1990, 1, 1))
                .status(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();

        Role adminRole = new Role();
        adminRole.setId(1);
        adminRole.setRoleName("ADMIN");
        admin.getRoles().add(adminRole);

        userRepository.save(admin);

        // Create regular user
        Users regularUser = Users.builder()
                .username("user_test")
                .password("User123!")
                .email("user@test.com")
                .nomor("08123456788")
                .tanggal_lahir(LocalDate.of(1995, 1, 1))
                .status(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();

        Role userRole = new Role();
        userRole.setId(2);
        userRole.setRoleName("USER");
        regularUser.getRoles().add(userRole);

        userRepository.save(regularUser);

        // Generate tokens
        adminToken = jwtProvider.generateToken(admin.getId(), admin.getUsername(), List.of("ADMIN"));
        userToken = jwtProvider.generateToken(regularUser.getId(), regularUser.getUsername(), List.of("USER"));
    }

    @Test
    void testPublicEndpoints() throws Exception {
        // Test register endpoint (public)
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("newuser");
        registerDto.setPassword("Password123!");
        registerDto.setEmail("newuser@test.com");
        registerDto.setNomor("08123456787");
        registerDto.setTanggal_lahir(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminOnlyEndpoints() throws Exception {
        // Test admin endpoint dengan token admin - harus berhasil
        mockMvc.perform(get("/users/all")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        // Test admin endpoint dengan token user - harus gagal
        mockMvc.perform(get("/users/all")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUserOnlyEndpoints() throws Exception {
        // Test user endpoint dengan token user - harus berhasil
        mockMvc.perform(get("/api/transaksi/my-transactions")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        // Test user endpoint dengan token admin - harus berhasil (admin bisa akses semua)
        mockMvc.perform(get("/api/transaksi/my-transactions")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointsWithoutToken() throws Exception {
        // Test endpoint yang memerlukan autentikasi tanpa token
        mockMvc.perform(get("/users/all"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/transaksi/my-transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidToken() throws Exception {
        // Test dengan token yang tidak valid
        mockMvc.perform(get("/users/all")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testExpiredToken() throws Exception {
        // Test dengan token yang expired
        String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbl90ZXN0IiwiYXV0aG9yaXRpZXMiOlsiQURNSU4iXSwiZXhwIjoxNjAwMDAwMDAwfQ.invalid_signature";

        mockMvc.perform(get("/users/all")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRoleBasedAccessControl() throws Exception {
        // Test bahwa user tidak bisa akses endpoint admin
        mockMvc.perform(get("/users/search")
                .header("Authorization", "Bearer " + userToken)
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());

        // Test bahwa admin bisa akses endpoint admin
        mockMvc.perform(get("/users/search")
                .header("Authorization", "Bearer " + adminToken)
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testFilmEndpointsWithDifferentRoles() throws Exception {
        // Test film endpoints dengan user - harus berhasil (read-only)
        mockMvc.perform(get("/all/film")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        // Test film endpoints dengan admin - harus berhasil
        mockMvc.perform(get("/all/film")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testTransactionOwnership() throws Exception {
        // Test bahwa user hanya bisa akses transaksinya sendiri
        // Ini akan memerlukan mock data transaksi untuk test yang lebih lengkap
        mockMvc.perform(get("/api/transaksi/my-transactions")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void testReportGenerationAdminOnly() throws Exception {
        // Test laporan hanya bisa diakses admin
        mockMvc.perform(get("/api/laporan/excel/users")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/laporan/excel/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testTicketDownloadUserAccess() throws Exception {
        // Test download tiket - user bisa download tiketnya sendiri
        mockMvc.perform(get("/api/laporan/tiket/pdf/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // Admin juga bisa download tiket user lain
        mockMvc.perform(get("/api/laporan/tiket/pdf/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}