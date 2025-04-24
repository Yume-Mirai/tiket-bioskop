package com.uasjava.tiketbioskop.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uasjava.tiketbioskop.dto.GenericResponse;
import com.uasjava.tiketbioskop.dto.LoginDto;
import com.uasjava.tiketbioskop.service.LoginService;

@RestController
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @PostMapping
    public ResponseEntity<Object> login(@RequestBody LoginDto dto){
        try {
            return ResponseEntity.ok().body(GenericResponse.builder()
                                        .success(true)
                                        .message("Successfully login")
                                        .data(loginService.login(dto))
                                        .build());

        } catch (ResponseStatusException rse) {
            return ResponseEntity.status(rse.getStatusCode())
                                        .body(GenericResponse.builder()
                                        .success(false)
                                        .message(rse.getReason())
                                        .data(null)
                                        .build());
        }catch(Exception e){
            // e.printStackTrace();
            return ResponseEntity.internalServerError()
                                        .body(GenericResponse.builder()
                                        .success(false)
                                        .message("Internal Server Error")
                                        .data(null)
                                        .build());
        }
    }

}
