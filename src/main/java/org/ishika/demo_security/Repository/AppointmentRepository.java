package org.ishika.demo_security.Repository;

import org.ishika.demo_security.model.Appointment;
import org.ishika.demo_security.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        Optional<Appointment> findByDoctorIdAndPatientId(Long doctorId, Long patientId);

        Optional<Appointment> findByDoctorIdAndPatientIdAndSlot(Long doctorId, Long patientId, String slot);

        void deleteByDoctorIdAndPatientId(Long doctorId, Long patientId);

        List<Appointment> findByPatientId(Long patientId);

        List<Appointment> findByDoctorId(Long doctorId);  // Add this for doctor dashboard

        List<Appointment> findByDoctorIdAndSlotAndStatusNot(Long doctorId, String slot, AppointmentStatus status);


        List<Appointment> findByDoctorIdAndStatusNot(Long doctorId, AppointmentStatus appointmentStatus);


        List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

        List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date);

        List<Appointment> findByDoctorIdAndStatusAndDate(Long doctorId, AppointmentStatus status, LocalDate date);

}
