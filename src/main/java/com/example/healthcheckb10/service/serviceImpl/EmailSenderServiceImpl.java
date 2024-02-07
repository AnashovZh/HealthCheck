package com.example.healthcheckb10.service.serviceImpl;

import com.example.healthcheckb10.exceptions.NotFoundException;
import com.example.healthcheckb10.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendEmail(String toEmail, String subject, String templateName, Context context) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,  "UTF-8");
            helper.setFrom("healthcheck05@gmail.com");
            helper.setTo(toEmail);
            String htmlContent=templateEngine.process(templateName,context);
            helper.setText(htmlContent, true);
            helper.setSubject(subject);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new NotFoundException("Не удалось отправить письмо!");
        } catch (NullPointerException e) {
            throw new NotFoundException("Не найдено и вернуто значение null!");
        }
    }
}