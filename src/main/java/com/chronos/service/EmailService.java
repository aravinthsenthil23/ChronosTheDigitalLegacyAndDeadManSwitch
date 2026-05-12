package com.chronos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ==========================================
    // 1. SEND WARNING EMAIL (To User)
    // ==========================================
    // Triggered when the timer is almost up (e.g., 3 days left)
    public void sendWarningEmail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("⚠️ Chronos Alert: Are you okay?");
        message.setText("Hello " + userName + ",\n\n" +
                "We haven't detected any activity from you recently.\n" +
                "Your Digital Legacy Vault is approaching its release trigger.\n\n" +
                "If you are reading this, please login immediately to RESET the timer:\n" +
                "http://localhost:8080/login.html\n\n" +
                "If you do not act, your files will be transferred to your nominee.\n\n" +
                "Stay safe,\nTeam Chronos");

        try {
            mailSender.send(message);
            System.out.println("📧 Warning Email Sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send Warning Email: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. SEND RELEASE EMAIL (To Nominee)
    // ==========================================
    // Triggered when the timer hits ZERO
    public void sendReleaseEmail(String nomineeEmail, String userName, String accessToken) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(nomineeEmail);
        message.setSubject("🚨 URGENT: Digital Legacy Release for " + userName);
        message.setText("Hello,\n\n" +
                "This is an automated message from the Chronos Dead Man's Switch.\n" +
                "The user '" + userName + "' has been inactive for the specified period.\n\n" +
                "As their trusted nominee, you have been granted access to their Digital Vault.\n\n" +
                "--- YOUR ACCESS TOKEN ---\n" +
                accessToken + "\n" +
                "-------------------------\n\n" +
                "Please use this token to download the secure files here:\n" +
                "http://localhost:8080/nominee.html\n\n" +
                "Regards,\nChronos Automated System");

        try {
            mailSender.send(message);
            System.out.println("📨 RELEASE Email Sent to Nominee: " + nomineeEmail);
        } catch (Exception e) {
            System.err.println("Failed to send Release Email: " + e.getMessage());
        }
    }
}