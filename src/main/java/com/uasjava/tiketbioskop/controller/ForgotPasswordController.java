package com.uasjava.tiketbioskop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uasjava.tiketbioskop.service.ForgotPasswordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/request")
    public ResponseEntity<String> requestOtp(@RequestParam String email) {
        forgotPasswordService.requestOtp(email);
        return ResponseEntity.ok("OTP telah dikirim ke email.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        forgotPasswordService.verifyOtp(email, otp);
        return ResponseEntity.ok("OTP valid. Anda dapat mereset password.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        forgotPasswordService.resetPassword(email, newPassword);
        return ResponseEntity.ok("Password berhasil diubah.");
    }
}

