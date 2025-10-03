package com.uasjava.tiketbioskop.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.dto.LoginDto;
import com.uasjava.tiketbioskop.dto.LoginResponseDto;
import com.uasjava.tiketbioskop.service.LoginService;

@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {
    
    @Autowired
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @PostMapping
    public ResponseEntity<GenericResponse<LoginResponseDto>> login(@RequestBody LoginDto dto){
        try {
            log.info("User mencoba login dengan username: {}", dto.getUsername());

            LoginResponseDto loginResponse = loginService.login(dto);

            log.info("Login berhasil untuk user: {}", dto.getUsername());

            return ResponseEntity.ok(GenericResponse.<LoginResponseDto>builder()
                                         .success(true)
                                         .message("Login berhasil! Token akses telah dikirim ke email Anda.")
                                         .data(loginResponse)
                                         .build());

        } catch (ResponseStatusException rse) {
            log.warn("Login gagal untuk username: {} - {}", dto.getUsername(), rse.getReason());
            return ResponseEntity.status(rse.getStatusCode())
                                         .body(GenericResponse.<LoginResponseDto>builder()
                                         .success(false)
                                         .message(rse.getReason())
                                         .data(null)
                                         .build());
        } catch(Exception e){
            log.error("Error saat login untuk username: {}", dto.getUsername(), e);
            return ResponseEntity.internalServerError()
                                         .body(GenericResponse.<LoginResponseDto>builder()
                                         .success(false)
                                         .message("Terjadi kesalahan internal server")
                                         .data(null)
                                         .build());
        }
    }

}
