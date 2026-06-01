package com.example.auth_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail ;
    public void sendWelcomeEmail(String toEmail , String name){
        SimpleMailMessage Message = new SimpleMailMessage();
        Message.setFrom(fromEmail);
        Message.setTo(toEmail);
        Message.setSubject("welCome to our Platform ");
        Message.setText("HELLO"+name+"\n\n thanks for registering with us !");
        mailSender.send(Message);
    }
    public void SendResetOtpEmail(String toEmail, String otp){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("password reset otp");
        mailMessage.setText("your otp for resetting your is "+otp +"use this otp to proceed with resetting your password ");
        mailSender.send(mailMessage);
    }
    public void SendOtpEmail(String toEmail, String otp){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Account verification otp");
        mailMessage.setText("your otp is :"+ otp+" verify otp account using this otp");
        mailSender.send(mailMessage);

    }

}
