package com.uasjava.tiketbioskop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uasjava.tiketbioskop.filter.JwtFilter;


@Configuration
public class SecurityConfig {

    @Autowired
    private final JwtFilter jwtFilter;
    
    public SecurityConfig(JwtFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                    // Security headers untuk React SPA
                    .frameOptions(frameOptions -> frameOptions.deny())
                    .contentTypeOptions(contentTypeOptions -> {})
                    .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                        .maxAgeInSeconds(31536000)
                        .includeSubDomains(true)
                    )
                )
                .authorizeHttpRequests(auth ->
                    auth
                        // Public endpoints - tidak memerlukan autentikasi
                        .requestMatchers("/login", "/users/register", "/email/send").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Static file serving untuk React
                        .requestMatchers("/uploads/**", "/static/**").permitAll()

                        // Endpoint film yang bisa diakses user (read-only)
                        .requestMatchers("/all/film", "/all/film/{id}", "/all/film/{id}/poster",
                                       "/all/film/genre", "/all/film/search", "/all/film/filter",
                                       "/all/film/advanced-search").hasAnyRole("USER", "ADMIN")

                        // Endpoint bioskop dan jadwal (read-only untuk user)
                        .requestMatchers("/all/bioskop", "/all/bioskop/{id}",
                                       "/all/jadwal", "/all/jadwal/{id}").hasAnyRole("USER", "ADMIN")

                        // Endpoint kursi untuk user
                        .requestMatchers("/all/kursi/available/**").hasAnyRole("USER", "ADMIN")

                        // Endpoint transaksi user
                        .requestMatchers("/api/transaksi/checkout", "/api/transaksi/konfirmasi",
                                       "/api/transaksi/cancel/{transaksiId}", "/api/transaksi/my-transactions",
                                       "/api/transaksi/filter", "/api/transaksi/search").hasRole("USER")

                        // Endpoint tiket user (download tiket sendiri)
                        .requestMatchers("/api/laporan/tiket/pdf/{transaksiId}", "/api/laporan/tiket/download/{transaksiId}").hasRole("USER")

                        // Admin-only endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/users/all", "/users/search", "/users/filter").hasRole("ADMIN")
                        .requestMatchers("/api/laporan/**").hasRole("ADMIN")

                        // Endpoint forgot password
                        .requestMatchers("/user/forgot-password/**").permitAll()

                        // Semua endpoint lainnya memerlukan autentikasi
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
