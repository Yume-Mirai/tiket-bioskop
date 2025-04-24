package com.uasjava.tiketbioskop.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.uasjava.tiketbioskop.model.OtpToken;
import com.uasjava.tiketbioskop.model.Users;
import com.uasjava.tiketbioskop.repository.OtpTokenRepository;
import com.uasjava.tiketbioskop.repository.UserRepository;
import com.uasjava.tiketbioskop.util.PasswordUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository usersRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final JavaMailSender mailSender;

    public void requestOtp(String email) {
        Users user = usersRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        String otp = String.valueOf(new Random().nextInt(899999) + 100000); // 6 digit OTP

        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .build();

        otpTokenRepository.save(token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP untuk Reset Password");
        message.setText("Kode OTP Anda: " + otp + "\nBerlaku selama 10 menit.");
        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String otp) {
        OtpToken token = otpTokenRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP tidak ditemukan"));

        if (!token.getOtp().equals(otp)) throw new RuntimeException("OTP salah");
        if (token.getExpirationTime().isBefore(LocalDateTime.now())) throw new RuntimeException("OTP kadaluarsa");

        token.setIsVerified(true);
        otpTokenRepository.save(token);
        return true;
    }

    public void resetPassword(String email, String newPassword) {
        OtpToken token = otpTokenRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP belum diverifikasi"));

        if (!token.getIsVerified()) throw new RuntimeException("OTP belum diverifikasi");

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        user.setPassword(PasswordUtil.hash(newPassword)); // hash kalau pakai encoder
        
        usersRepository.save(user);
    }
}

