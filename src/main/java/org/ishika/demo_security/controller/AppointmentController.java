package org.ishika.demo_security.controller;

import org.ishika.demo_security.Repository.AppointmentRepository;
import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.Appointment;
import org.ishika.demo_security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/cancel")
    @ResponseBody
    public String cancelAppointment(@RequestParam Long doctorId, @RequestParam Long patientId) {
        appointmentRepository.deleteByDoctorIdAndPatientId(doctorId, patientId);
        return "success";
    }

    @PostMapping("/appointment/withdraw")
    @ResponseBody
    public String withdrawAppointment(@RequestParam Long appointmentId, Principal principal) {
        // Optional: Validate that the logged-in user owns the appointment before deleting
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return "Appointment not found";
        }
        Appointment appointment = appointmentOpt.get();
        // Check if current user is the patient of this appointment
        User currentUser = userRepository.findByEmail(principal.getName());
        if (!appointment.getPatientId().equals(currentUser.getId())) {
            return "Unauthorized";
        }
        appointmentRepository.deleteById(appointmentId);
        return "success";
    }
}
