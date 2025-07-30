package org.ishika.demo_security.service;

import org.ishika.demo_security.Repository.AppointmentRepository;
import org.ishika.demo_security.model.Appointment;
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

    // Add more logic as needed
}
