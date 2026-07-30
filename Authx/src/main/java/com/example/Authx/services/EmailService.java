package com.example.Authx.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

public void sendVerificationEmail(String toEmail , String token){
    String verifyLink = frontendUrl +"/verify-email?token="+token;

    SimpleMailMessage mailMessage= new SimpleMailMessage();
    mailMessage.setFrom(fromEmail);
    mailMessage.setTo(toEmail);
    mailMessage.setSubject("VERIFY YOUR AUTHX ACCOUNT");
    mailMessage.setText("Hi,\n\n" +
            "Thanks for registering. Click the link below to verify your email:\n\n" +
            verifyLink + "\n\n" +
            "This link expires in 24 hours.\n\n" +
            "If you didn't register, ignore this email.");
    mailSender.send(mailMessage);

}

public void sendPasswordResetEmail(String toEmail,String token){
    String resetLink = frontendUrl + "/reset-password?token=" + token;

    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setFrom(fromEmail);
    mailMessage.setTo(toEmail);
    mailMessage.setSubject("RESET YOUR AUTHX PASSWORD ");
    mailMessage.setText(
            "Hi,\n\n" +
                    "You requested a password reset. Click the link below:\n\n" +
                    resetLink + "\n\n" +
                    "This link expires in 15 minutes.\n\n" +
                    "If you didn't request this, ignore this email — your password won't change."
    );
    mailSender.send(mailMessage);
}

public void sendSuspiciousLoginAlert(
        String toEmail,
        String name,
        String device,
        String os,
        String ipAddress,
        LocalDateTime loginTime
){
    try{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,true,"UTF-8"
        );

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(
                "⚠️ New login detected on your AuthX account"
        );
//  format time

        String formattedTime = loginTime.format(
                DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
        );

//        body

        String html = """
                
                <div style="font-family: Arial, sans-serif;
                                            max-width: 600px; margin: 0 auto;
                                            padding: 20px;">
                
                                  <h2 style="color: #1a1a2e;">
                                    New Login Detected
                                  </h2>
                
                                  <p>Hi <strong>%s</strong>,</p>
                
                                  <p>We noticed a new login to your
                                     <strong>AuthX</strong> account.</p>
                
                                  <div style="background: #f8f9fa;
                                              border-left: 4px solid #e74c3c;
                                              padding: 16px;
                                              border-radius: 4px;
                                              margin: 20px 0;">
                                    <p style="margin: 4px 0;">
                                      <strong>Device:</strong> %s / %s
                                    </p>
                                    <p style="margin: 4px 0;">
                                      <strong>IP Address:</strong> %s
                                    </p>
                                    <p style="margin: 4px 0;">
                                      <strong>Time:</strong> %s
                                    </p>
                                  </div>
                
                                  <p>If this was you, no action needed.</p>
                
                                  <p>If this wasn't you, secure your account
                                     immediately:</p>
                
                                  <a href="%s/forgot-password"
                                     style="display: inline-block;
                                            background: #e74c3c;
                                            color: white;
                                            padding: 12px 24px;
                                            border-radius: 6px;
                                            text-decoration: none;
                                            font-weight: bold;
                                            margin: 10px 0;">
                                    Secure My Account
                                  </a>
                
                                  <p style="color: #888; font-size: 12px;
                                             margin-top: 30px;">
                                    This email was sent by AuthX Security.
                                    Please do not reply.
                                  </p>
                                </div>
                """.formatted(
                        name != null ? name : "User",
                device,
                os,
                ipAddress,
                formattedTime,
                frontendUrl
        );
        helper.setText(html,true);
        mailSender.send(message);


        log.info("Suspicious login alert sent to: {}",
                toEmail);

    } catch (MessagingException e) {
        log.error("Failed to send suspicious login alert: {}",
                e.getMessage());
    }
}

}
