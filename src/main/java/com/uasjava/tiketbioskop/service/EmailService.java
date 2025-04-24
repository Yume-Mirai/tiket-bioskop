package com.uasjava.tiketbioskop.service;

import java.util.Map;

import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendPaymentEmail(String to, String subject, String templateName, Context context) throws MessagingException;
}
