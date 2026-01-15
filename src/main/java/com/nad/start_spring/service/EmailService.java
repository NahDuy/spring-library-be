package com.nad.start_spring.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email, String code) {
        String subject = "Your verification code";
        String content = "Your code is: " + code + "\nIt will expire in 5 minutes.";
        sendEmail(email, subject, content);
    }

    public void sendEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, false); // false = plain text

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Async
    public void sendNotification(String toEmail, String title, String message) {
        try {
            String subject = "[Thư viện] " + title;
            String htmlContent = String.format(
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                "<div style='background-color: white; max-width: 600px; margin: 20px auto; padding: 20px; border-radius: 5px;'>" +
                "<h2 style='color: #333;'>%s</h2>" +
                "<p style='color: #666; line-height: 1.6;'>%s</p>" +
                "<hr style='border: none; border-top: 1px solid #ddd;'>" +
                "<p style='color: #999; font-size: 12px;'>Đây là thông báo tự động từ hệ thống thư viện. Vui lòng không reply email này.</p>" +
                "</div>" +
                "</body>" +
                "</html>",
                title, message
            );
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            
            mailSender.send(mimeMessage);
            log.info("Notification email sent successfully to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send notification email to {}: {}", toEmail, e.getMessage());
        }
    }
}
