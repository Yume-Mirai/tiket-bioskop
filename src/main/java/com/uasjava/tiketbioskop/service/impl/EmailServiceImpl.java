package com.uasjava.tiketbioskop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.uasjava.tiketbioskop.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.username:noreply@tiketbioskop.com}")
    private String fromEmail;

    @Value("${spring.application.name:Tiket Bioskop}")
    private String appName;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            if (to == null || to.trim().isEmpty()) {
                log.warn("Email tidak dikirim karena alamat email kosong");
                return;
            }

            if (!isValidEmail(to)) {
                log.warn("Email tidak dikirim karena format email tidak valid: {}", to);
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject != null ? subject : "Notifikasi");
            message.setText(body != null ? body : "");

            javaMailSender.send(message);
            log.info("Email berhasil dikirim ke: {}", to);

        } catch (Exception e) {
            log.error("Gagal mengirim email ke {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Gagal mengirim email", e);
        }
    }

    @Override
    public void sendPaymentEmail(String to, String subject, String templateName, Context context)
            throws MessagingException {
        try {
            if (to == null || to.trim().isEmpty()) {
                log.warn("Email pembayaran tidak dikirim karena alamat email kosong");
                return;
            }

            if (!isValidEmail(to)) {
                log.warn("Email pembayaran tidak dikirim karena format email tidak valid: {}", to);
                return;
            }

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            if (context == null) {
                context = new Context();
            }

            // Tambahkan informasi aplikasi ke context
            context.setVariable("appName", appName);
            context.setVariable("currentYear", java.time.LocalDate.now().getYear());

            String html = templateEngine.process(templateName, context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject != null ? subject : "Konfirmasi Pembayaran");
            helper.setText(html, true);

            javaMailSender.send(message);
            log.info("Email pembayaran berhasil dikirim ke: {}", to);

        } catch (MessagingException e) {
            log.error("Gagal mengirim email pembayaran ke {}: {}", to, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error tidak terduga saat mengirim email pembayaran ke {}: {}", to, e.getMessage(), e);
            throw new MessagingException("Gagal mengirim email pembayaran", e);
        }
    }

    /**
     * Validasi format email sederhana
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
