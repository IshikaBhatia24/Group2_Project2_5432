package org.ishika.demo_security.service;

import jakarta.mail.MessagingException;
import org.ishika.demo_security.Repository.AppointmentRepository;
import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.Appointment;
import org.ishika.demo_security.model.AppointmentStatus;
import org.ishika.demo_security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public void save(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public boolean existsByDoctorIdAndPatientIdAndSlot(Long doctorId, Long patientId, String slot) {
        return appointmentRepository
                .findByDoctorIdAndPatientIdAndSlot(doctorId, patientId, slot)
                .isPresent();
    }

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    public void confirmAppointment(Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid appointment ID"));

        appt.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt);

        User patient = userRepository.findById(appt.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient ID"));

        try {
            emailService.sendAppointmentConfirmedEmail(
                    patient.getEmail(),
                    patient.getFullName(),
                    appt.getDoctorName(),
                    appt.getSlot()
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid appointment ID"));
        appt.setStatus(AppointmentStatus.valueOf(status));
        appointmentRepository.save(appt);

        User patient = userRepository.findById(appt.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient ID"));
        try {
            emailService.sendAppointmentStatusChangedEmail(
                    patient.getEmail(),
                    patient.getFullName(),
                    appt.getDoctorName(),
                    appt.getSlot(),
                    appt.getStatus()
            );
        } catch (MessagingException e) {
            e.printStackTrace(); // Or better, use a logger to see any issues
        }
    }



    // Add more logic as needed
}
