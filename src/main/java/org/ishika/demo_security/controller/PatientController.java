package org.ishika.demo_security.controller;

import org.ishika.demo_security.Repository.AppointmentRepository;
import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import org.ishika.demo_security.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public PatientController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String patientDashboard() {
        return "patient-dashboard";
    }


    @GetMapping("/doctors")
    public String showDoctorBookingPage(Model model) {
        List<User> doctors = userRepository.findByRole("ROLE_DOCTOR");
        System.out.println("Doctors found: " + doctors.size());
        model.addAttribute("doctors", doctors);
        return "doctor-list";
    }

    @PostMapping("/book")
    @ResponseBody
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam String slot,
                                  @RequestParam String emergency,
                                  @RequestParam String details,
                                  Principal principal) {

        User doctor = userRepository.findById(doctorId).orElse(null);
        User patient = userRepository.findByEmail(principal.getName());

        if (doctor == null || patient == null) return "Invalid doctor or patient.";

        Appointment appt = new Appointment();
        appt.setDoctorId(doctor.getId());
        appt.setDoctorName(doctor.getFullName());
        appt.setSpecialization(doctor.getSpecialization());
        appt.setSlot(slot);
        appt.setEmergency(emergency);
        appt.setDetails(details);
        appt.setPatientId(patient.getId());

        appointmentRepository.save(appt);
        return "success";
    }

    @PostMapping("/request-appointment")
    public String handleAppointmentRequest(@RequestParam Long doctorId,
                                           @RequestParam String slot,
                                           @RequestParam String emergency,
                                           @RequestParam(required = false) String details) {
        // TODO: Create and save Appointment entity (or AppointmentRequest)

        // Optional: You can log for now to verify it's called
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Slot: " + slot);
        System.out.println("Emergency: " + emergency);
        System.out.println("Details: " + details);


        return "redirect:/patient/dashboard";
        }


    }