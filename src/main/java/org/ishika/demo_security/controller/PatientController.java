package org.ishika.demo_security.controller;

import org.ishika.demo_security.Repository.AppointmentRepository;
import org.ishika.demo_security.Repository.PatientProfileRepository;
import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

//    public PatientController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @GetMapping("/dashboard")
    public String patientDashboard(Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());

        model.addAttribute("appointments", appointmentRepository.findByPatientId(currentUser.getId()));

        // Add full name attribute to model
        model.addAttribute("patientName", currentUser.getFullName());

        return "patient-dashboard";
    }




    @GetMapping("/doctors")
    public String showDoctorBookingPage(Model model) {
        List<User> doctors = userRepository.findByRole("ROLE_DOCTOR");
        System.out.println("Doctors found: " + doctors.size());

        for (User doctor : doctors) {
            if (doctor.getDoctorProfile() != null) {
                // force proxy initialization, or access field to ensure loaded
                doctor.getDoctorProfile().getSpecialization();
            }
        }
        model.addAttribute("doctors", doctors);
        return "doctor-list";
    }

    @PostMapping("/book")
    @ResponseBody
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam String slot,
                                  @RequestParam String date,
                                  @RequestParam String emergency,
                                  @RequestParam String details,
                                  Principal principal) {

        User doctor = userRepository.findById(doctorId).orElse(null);
        User patient = userRepository.findByEmail(principal.getName());

        if (doctor == null || patient == null) return "Invalid doctor or patient.";

        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndSlotAndStatusNot(doctorId, slot, AppointmentStatus.CANCELLED);

        if (!existingAppointments.isEmpty()) {
            return "Appointment slot is already taken.";
        }


        Appointment appt = new Appointment();
        appt.setDoctorId(doctor.getId());
        appt.setDoctorName(doctor.getFullName());
        //appt.setSpecialization(doctor.getSpecialization());
        if (doctor.getDoctorProfile() != null) {
            appt.setSpecialization(doctor.getDoctorProfile().getSpecialization());
        } else {
            appt.setSpecialization("N/A");
        }

        appt.setSlot(slot);
        appt.setEmergency(emergency);
        appt.setDetails(details);
        appt.setPatientId(patient.getId());

        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        appt.setDate(parsedDate);

        appt.setStatus(AppointmentStatus.PENDING);

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

    @GetMapping("/profile")
    public String patientProfile(Model model, Principal principal) {
        // Fetch current user
        User user = userRepository.findByEmail(principal.getName());

        // If you use composition with profile object:
        PatientProfile patientProfile = patientProfileRepository.findByUser(user);

        // Create a wrapper object to send to the template
        PatientRegistrationForm form = new PatientRegistrationForm();
        form.setUser(user);
        form.setPatientProfile(patientProfile);

        model.addAttribute("form", form);

        return "patient-profile";
    }


    @PostMapping("/profile")
    public String updatePatientProfile(@ModelAttribute("patient") User updatedPatient, Principal principal) {
        // Load current patient from DB
        User patient = userRepository.findByEmail(principal.getName());
        if (patient == null) {
            // handle error (redirect or message)
            return "redirect:/patient/profile?error";
        }

        // Update editable fields - be careful not to overwrite sensitive fields
        patient.setFullName(updatedPatient.getFullName());
        patient.setPhoneNumber(updatedPatient.getPhoneNumber());
        // You can add more editable fields if you want

        // Save updated patient data
        userRepository.save(patient);

        return "redirect:/patient/profile?success";
    }





}