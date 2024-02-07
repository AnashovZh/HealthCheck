package com.example.healthcheckb10.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public interface EmailSenderService {
    void sendEmail(String toEmail, String subject, String templateName, Context context);
}