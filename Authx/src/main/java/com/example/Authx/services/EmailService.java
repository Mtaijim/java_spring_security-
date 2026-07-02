package com.example.Authx.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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


}
