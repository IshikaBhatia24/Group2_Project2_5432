package org.ishika.demo_security.controller;

import org.ishika.demo_security.Repository.AppointmentRepository;
import org.ishika.demo_security.Repository.PatientProfileRepository;
import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/dashboard")
    public String patientDashboard(Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());

        model.addAttribute("appointments", appointmentRepository.findByPatientId(currentUser.getId()));

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

        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);

        User doctor = userRepository.findById(doctorId).orElse(null);
        User patient = userRepository.findByEmail(principal.getName());

        if (doctor == null || patient == null) {
            return "Invalid doctor or patient.";
        }

        boolean slotTaken = !appointmentRepository
                .findByDoctorIdAndDateAndSlotAndStatusNot(
                        doctorId, parsedDate, slot, AppointmentStatus.CANCELLED
                ).isEmpty();

        if (slotTaken) {
            return "This slot is already booked by another patient.";
        }

        // Create and save new appointment
        Appointment appt = new Appointment();
        appt.setDoctorId(doctor.getId());
        appt.setDoctorName(doctor.getFullName());
        appt.setSpecialization(
                doctor.getDoctorProfile() != null
                        ? doctor.getDoctorProfile().getSpecialization()
                        : "N/A"
        );
        appt.setSlot(slot);
        appt.setEmergency(emergency);
        appt.setDetails(details);
        appt.setPatientId(patient.getId());
        appt.setDate(parsedDate);
        appt.setStatus(AppointmentStatus.PENDING); // starts as pending

        appointmentRepository.save(appt);

        return "success";
    }


    @PostMapping("/request-appointment")
    public String handleAppointmentRequest(@RequestParam Long doctorId,
                                           @RequestParam String slot,
                                           @RequestParam String emergency,
                                           @RequestParam(required = false) String details) {

        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Slot: " + slot);
        System.out.println("Emergency: " + emergency);
        System.out.println("Details: " + details);


        return "redirect:/patient/dashboard";
        }

    @GetMapping("/check-slot")
    @ResponseBody
    public boolean checkSlot(@RequestParam Long doctorId,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestParam String slot) {
        return appointmentRepository
                .existsByDoctorIdAndDateAndSlotAndStatusNot(
                        doctorId, date, slot, AppointmentStatus.CANCELLED
                );
    }

    @DeleteMapping("/delete-appointment/{id}")
    @ResponseBody
    public String deleteAppointment(@PathVariable Long id, Principal principal) {
        User patient = userRepository.findByEmail(principal.getName());

        Optional<Appointment> apptOpt = appointmentRepository.findById(id);
        if (apptOpt.isEmpty() || !apptOpt.get().getPatientId().equals(patient.getId())) {
            return "error"; // not found or not owned by this patient
        }

        appointmentRepository.deleteById(id);
        return "success";
    }

    @GetMapping("/profile")
    public String patientProfile(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());

        PatientProfile patientProfile = patientProfileRepository.findByUser(user);

        PatientRegistrationForm form = new PatientRegistrationForm();
        form.setUser(user);
        form.setPatientProfile(patientProfile);

        model.addAttribute("form", form);

        return "patient-profile";
    }


    @PostMapping("/profile")
    public String updatePatientProfile(@ModelAttribute("form") PatientRegistrationForm form, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName());
        if (currentUser == null) {
            return "redirect:/patient/profile?error";
        }

        currentUser.setPhoneNumber(form.getUser().getPhoneNumber());

        userRepository.save(currentUser);

        PatientProfile currentProfile = currentUser.getPatientProfile();
        if (currentProfile != null && form.getPatientProfile() != null) {
            PatientProfile formProfile = form.getPatientProfile();
            currentProfile.setAge(formProfile.getAge());
            currentProfile.setGender(formProfile.getGender());
            currentProfile.setCity(formProfile.getCity());
            currentProfile.setState(formProfile.getState());
            currentProfile.setCountry(formProfile.getCountry());
            currentProfile.setZipCode(formProfile.getZipCode());
            currentProfile.setEmergencyContact(formProfile.getEmergencyContact());

            patientProfileRepository.save(currentProfile);
        }

        return "redirect:/patient/profile?success";
    }

    @GetMapping("/appointments")
    public String patientAppointments(Model model, Principal principal) {
        // Fetch current patient
        User currentUser = userRepository.findByEmail(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error";
        }

        // Fetch all appointments for this patient
        List<Appointment> appointments = appointmentRepository.findByPatientId(currentUser.getId());

        model.addAttribute("appointments", appointments);
        model.addAttribute("patientName", currentUser.getFullName());

        return "patient-appointments"; // Thymeleaf template for appointments page
    }

    @GetMapping("/billing")
    public String patientBilling() {
        return "patient-billing";
    }

    @GetMapping("/medical-records")
    public String patientMedicalRecords() {
        return "patient-medical-records";
    }



}