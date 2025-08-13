package org.ishika.demo_security.controller;

import org.ishika.demo_security.Repository.PatientProfileRepository;
import org.ishika.demo_security.Repository.DoctorProfileRepository;
import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private PatientRegistrationForm patientRegisterationForm;
    private DoctorRegistrationForm doctorRegistrationForm;

    public AuthController(UserRepository userRepository,
                          PatientProfileRepository patientProfileRepository,
                          DoctorProfileRepository doctorProfileRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterOptions() {
        return "register";  // This will serve your register.html with buttons to choose patient or doctor registration
    }

    @GetMapping("/register/patient")
    public String showPatientRegisterForm(Model model) {
        PatientRegistrationForm form = new PatientRegistrationForm();
        form.setUser(new User());
        form.setPatientProfile(new PatientProfile());
        model.addAttribute("form", form);
        return "patient-register";
    }

    @PostMapping("/register/patient")
    public String registerPatient(@ModelAttribute("form") PatientRegistrationForm form, Model model) {
        User user = form.getUser();
        // Check if email exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "A user with this email already exists.");
            return "patient-register"; // Show registration page with error
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_PATIENT");
        userRepository.save(user);
        PatientProfile profile = form.getPatientProfile();
        profile.setUser(user); // link user to profile
        patientProfileRepository.save(profile);
        return "redirect:/login";
    }


    @GetMapping("/register/doctor")
    public String showDoctorRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("doctorProfile", new DoctorProfile());
        return "doctor-register"; // new Thymeleaf template for doctor registration with profile fields
    }

    @PostMapping("/register/doctor")
    public String registerDoctor(@ModelAttribute User user, @ModelAttribute DoctorProfile doctorProfile, Model model) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "A user with this email already exists.");
            return "doctor-register"; // Show registration page with error
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_DOCTOR");
        User savedUser = userRepository.save(user);
        doctorProfile.setUser(savedUser);
        doctorProfileRepository.save(doctorProfile);
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
