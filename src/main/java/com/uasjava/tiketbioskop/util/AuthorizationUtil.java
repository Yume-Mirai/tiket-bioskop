package com.uasjava.tiketbioskop.util;

import com.uasjava.tiketbioskop.dto.UserCredentialsDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class untuk memvalidasi otorisasi pengguna
 */
public class AuthorizationUtil {

    /**
     * Mendapatkan informasi kredensial pengguna yang sedang login
     */
    public static UserCredentialsDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof UserCredentialsDto) {
            return (UserCredentialsDto) authentication.getCredentials();
        }
        return null;
    }

    /**
     * Mendapatkan username pengguna yang sedang login
     */
    public static String getCurrentUsername() {
        UserCredentialsDto user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * Mendapatkan user ID pengguna yang sedang login
     */
    public static Integer getCurrentUserId() {
        UserCredentialsDto user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * Mendapatkan roles pengguna yang sedang login
     */
    public static List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Memeriksa apakah pengguna memiliki role tertentu
     */
    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role.toUpperCase());
    }

    /**
     * Memeriksa apakah pengguna memiliki salah satu dari roles yang diberikan
     */
    public static boolean hasAnyRole(String... roles) {
        List<String> userRoles = getCurrentUserRoles();
        return java.util.Arrays.stream(roles)
                .anyMatch(role -> userRoles.contains(role.toUpperCase()));
    }

    /**
     * Memeriksa apakah pengguna adalah admin
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Memeriksa apakah pengguna adalah user biasa
     */
    public static boolean isUser() {
        return hasRole("USER");
    }

    /**
     * Memeriksa apakah pengguna memiliki akses ke resource milik user lain
     * Hanya admin yang bisa akses resource user lain
     */
    public static boolean canAccessUserResource(Integer targetUserId) {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null || targetUserId == null) {
            return false;
        }

        // User hanya bisa akses resource sendiri, admin bisa akses semua
        return currentUserId.equals(targetUserId) || isAdmin();
    }

    /**
     * Validasi bahwa pengguna saat ini adalah pemilik resource atau admin
     */
    public static void validateOwnershipOrAdmin(Integer resourceOwnerId) {
        if (!canAccessUserResource(resourceOwnerId)) {
            throw new SecurityException("Akses ditolak: Anda tidak memiliki izin untuk mengakses resource ini");
        }
    }

    /**
     * Validasi bahwa pengguna memiliki role yang diperlukan
     */
    public static void validateRole(String requiredRole) {
        if (!hasRole(requiredRole)) {
            throw new SecurityException("Akses ditolak: Role " + requiredRole + " diperlukan");
        }
    }

    /**
     * Validasi bahwa pengguna memiliki salah satu role yang diperlukan
     */
    public static void validateAnyRole(String... requiredRoles) {
        if (!hasAnyRole(requiredRoles)) {
            String roles = String.join(", ", requiredRoles);
            throw new SecurityException("Akses ditolak: Salah satu role berikut diperlukan: " + roles);
        }
    }
}