package com.uasjava.tiketbioskop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.dto.RegisterUserDto;
import com.uasjava.tiketbioskop.service.UserService;

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

}

