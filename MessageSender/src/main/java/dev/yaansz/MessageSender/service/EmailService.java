package dev.yaansz.MessageSender.service;

import dev.yaansz.MessageSender.dto.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@example.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(MessageDTO message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(message.getEmail());
            mailMessage.setSubject("Notification");
            mailMessage.setText(message.getBody());

            mailSender.send(mailMessage);
            logger.info("Successfully sent email to {} for message {}", message.getEmail(), message.getId());
        } catch (Exception e) {
            logger.error("Failed to send email to {} for message {}: {}", 
                message.getEmail(), message.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
