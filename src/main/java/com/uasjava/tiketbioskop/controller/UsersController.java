package com.uasjava.tiketbioskop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.dto.RegisterUserDto;
import com.uasjava.tiketbioskop.dto.UserDetailDto;
import com.uasjava.tiketbioskop.service.UserService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/users")
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
  public ResponseEntity<GenericResponse<Page<UserDetailDto>>> getAllUsers(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "id") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {
      return ResponseEntity.ok(GenericResponse.<Page<UserDetailDto>>builder()
          .success(true)
          .message("Berhasil mengambil data user")
          .data(userService.getAllUsers(page, size, sortBy, sortDir))
          .build());
  }

  @GetMapping("/search")
  @Operation(summary = "Mencari user berdasarkan username atau email dengan pagination")
  public ResponseEntity<GenericResponse<Page<UserDetailDto>>> searchUsers(
          @RequestParam String keyword,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "username") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {
      return ResponseEntity.ok(GenericResponse.<Page<UserDetailDto>>builder()
          .success(true)
          .message("Berhasil mencari data user")
          .data(userService.searchUsers(keyword, page, size, sortBy, sortDir))
          .build());
  }

  @GetMapping("/filter")
  @Operation(summary = "Filter user berdasarkan status dengan pagination")
  public ResponseEntity<GenericResponse<Page<UserDetailDto>>> filterUsersByStatus(
          @RequestParam Boolean status,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "username") String sortBy,
          @RequestParam(defaultValue = "asc") String sortDir) {
      return ResponseEntity.ok(GenericResponse.<Page<UserDetailDto>>builder()
          .success(true)
          .message("Berhasil filter data user")
          .data(userService.filterUsersByStatus(status, page, size, sortBy, sortDir))
          .build());
  }

}

