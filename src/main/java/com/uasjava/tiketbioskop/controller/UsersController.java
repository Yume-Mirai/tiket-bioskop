package com.uasjava.tiketbioskop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.dto.RegisterUserDto;
import com.uasjava.tiketbioskop.dto.UserDetailDto;
import com.uasjava.tiketbioskop.service.UserService;
import com.uasjava.tiketbioskop.util.AuthorizationUtil;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {

  @Autowired
  private final UserService userService;

  UsersController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<GenericResponse<Object>> register (
                                                @RequestBody RegisterUserDto dto) {
    userService.register(dto);

    return ResponseEntity.ok().body(GenericResponse.builder()
          .success(true)
          .message("Berhasil menambahkan user baru")
          .data(null)
          .build()
      );
  }

  @GetMapping("/all")
  @Operation(summary = "Menampilkan semua user dengan pagination dan sorting")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GenericResponse<Page<UserDetailDto>>> getAllUsers(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10000") int size,
          @RequestParam(defaultValue = "id") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {
      try {
          log.info("Admin mengambil semua data user");
          return ResponseEntity.ok(GenericResponse.<Page<UserDetailDto>>builder()
              .success(true)
              .message("Berhasil mengambil data user")
              .data(userService.getAllUsers(page, size, sortBy, sortDir))
              .build());
      } catch (Exception e) {
          log.error("Error saat mengambil data user: {}", e.getMessage(), e);
          throw new RuntimeException("Gagal mengambil data user");
      }
  }

  @GetMapping("/search")
  @Operation(summary = "Mencari user berdasarkan username atau email dengan pagination")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GenericResponse<Page<UserDetailDto>>> searchUsers(
          @RequestParam String keyword,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10000") int size,
          @RequestParam(defaultValue = "username") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {
      try {
          log.info("Admin mencari user dengan keyword: {}", keyword);
          return ResponseEntity.ok(GenericResponse.<Page<UserDetailDto>>builder()
              .success(true)
              .message("Berhasil mencari data user")
              .data(userService.searchUsers(keyword, page, size, sortBy, sortDir))
              .build());
      } catch (Exception e) {
          log.error("Error saat mencari user: {}", e.getMessage(), e);
          throw new RuntimeException("Gagal mencari data user");
      }
  }

  @GetMapping("/filter")
  @Operation(summary = "Filter user berdasarkan status dengan pagination")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GenericResponse<Page<UserDetailDto>>> filterUsersByStatus(
          @RequestParam Boolean status,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10000") int size,
          @RequestParam(defaultValue = "username") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {
      try {
          log.info("Admin filter user berdasarkan status: {}", status);
          return ResponseEntity.ok(GenericResponse.<Page<UserDetailDto>>builder()
              .success(true)
              .message("Berhasil filter data user")
              .data(userService.filterUsersByStatus(status, page, size, sortBy, sortDir))
              .build());
      } catch (Exception e) {
          log.error("Error saat filter user: {}", e.getMessage(), e);
          throw new RuntimeException("Gagal filter data user");
      }
  }

}

