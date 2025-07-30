package org.ishika.demo_security.Repository;

import org.ishika.demo_security.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        Optional<Appointment> findByDoctorIdAndPatientId(Long doctorId, Long patientId);
        Optional<Appointment> findByDoctorIdAndPatientIdAndSlot(Long doctorId, Long patientId, String slot);
        void deleteByDoctorIdAndPatientId(Long doctorId, Long patientId);
        List<Appointment> findByPatientId(Long patientId);



}

