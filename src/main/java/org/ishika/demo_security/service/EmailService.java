package org.ishika.demo_security.service;

import org.ishika.demo_security.model.AppointmentStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAppointmentConfirmedEmail(String toEmail, String patientName, String doctorName, String slot) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject("Appointment Confirmed");
        String content = "Dear " + patientName + ",<br><br>" +
                "Your appointment with Dr. " + doctorName + " has been confirmed for the slot: " + slot + ".<br>" +
                "Thank you.<br><br>Regards,<br>Healthcare Team";
        helper.setText(content, true);
        mailSender.send(message);
    }

    public void sendAppointmentStatusChangedEmail(
            String toEmail, String patientName, String doctorName,
            String slot, AppointmentStatus status
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject("Appointment Status Updated");

        String content = "Dear " + patientName + ", " +
                "Your appointment with Dr. " + doctorName +
                " for the slot: " + slot +
                " status has been updated to: " + status + ". " +
                "Thank you. Regards, Healthcare Team";

        helper.setText(content, true);
        mailSender.send(message);
    }

}
