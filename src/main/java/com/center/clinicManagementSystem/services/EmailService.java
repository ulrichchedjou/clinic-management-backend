package com.center.clinicManagementSystem.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:no-reply@clinic.com}")
    private String fromAddress;

    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Réinitialisation de votre mot de passe";
        String body = "Bonjour,\n\nPour réinitialiser votre mot de passe, veuillez utiliser ce lien ou ce code : " + resetToken + "\n\nSi vous n'êtes pas à l'origine de cette demande, ignorez cet email.";
        sendEmail(to, subject, body);
    }

    public void sendPatientNotification(String to, String subject, String message) {
        // For example: appointment reminders, test results, etc.
        sendEmail(to, subject, message);
    }

    public void sendAppointmentReminder(String to, String patientName, String date, String time, String doctorName, String location) {
        Context context = new Context();
        context.setVariable("patientName", patientName);
        context.setVariable("date", date);
        context.setVariable("time", time);
        context.setVariable("doctorName", doctorName);
        context.setVariable("location", location);

        String htmlContent = templateEngine.process("appointment-reminder.html", context);

        sendHtmlEmail(to, "Rappel de rendez-vous", htmlContent);
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromAddress);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Handle error (log, rethrow, etc.)
            System.err.printf("Failed to send email to %s: %s%n", to, e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(htmlBody, true); // true = HTML
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromAddress);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.printf("Failed to send email to %s: %s%n", to, e.getMessage());
        }
    }
} 