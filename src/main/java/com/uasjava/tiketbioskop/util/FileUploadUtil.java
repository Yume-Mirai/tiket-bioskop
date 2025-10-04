package com.uasjava.tiketbioskop.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Utility class untuk handling file upload dengan validasi dan keamanan
 */
@Component
public class FileUploadUtil {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:10MB}")
    private String maxFileSize;

    @Value("${app.upload.allowed-extensions:jpg,jpeg,png,gif,webp}")
    private String allowedExtensions;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Upload file dengan validasi lengkap
     */
    public String uploadFile(MultipartFile file, String subfolder) throws IOException {
        validateFile(file);

        // Buat direktori jika belum ada
        Path uploadPath = Paths.get(uploadDir, subfolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate nama file unik
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        // Copy file ke lokasi tujuan
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return subfolder + "/" + uniqueFilename;
    }

    /**
     * Validasi file upload
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File tidak boleh kosong");
        }

        // Validasi ukuran file
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("Ukuran file terlalu besar. Maksimal " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        // Validasi tipe konten
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IOException("Tipe file tidak didukung. Hanya JPG, PNG, GIF, dan WebP yang diperbolehkan");
        }

        // Validasi ekstensi file
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new IOException("Nama file tidak valid");
        }

        String extension = getFileExtension(filename).toLowerCase();
        List<String> allowedExts = Arrays.asList(allowedExtensions.toLowerCase().split(","));
        if (!allowedExts.contains(extension)) {
            throw new IOException("Ekstensi file tidak didukung: " + extension);
        }
    }

    /**
     * Mendapatkan ekstensi file dari nama file
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Menghapus file dari filesystem
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Mendapatkan path lengkap file
     */
    public Path getFilePath(String filePath) {
        return Paths.get(uploadDir, filePath);
    }

    /**
     * Mengecek apakah file exists
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(uploadDir, filePath));
    }
}