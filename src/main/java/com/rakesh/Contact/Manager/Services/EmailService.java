package com.rakesh.Contact.Manager.Services;

import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendMail(String to, String subject, String body);

    void sendMail(String to, String subject, String body, MultipartFile attachment) throws MessagingException;
}
