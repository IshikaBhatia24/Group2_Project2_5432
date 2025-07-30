package org.ishika.demo_security.controller;

import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/register")
    public String showRegisterOptions() {
        return "register";  // This will serve your register.html with buttons to choose patient or doctor registration
    }

    @GetMapping("/register/patient")
    public String showPatientRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "patient-register"; // new template for patient registration
    }

    @PostMapping("/register/patient")
    public String registerPatient(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_PATIENT");
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/register/doctor")
    public String showDoctorRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "doctor-register"; // new template for doctor registration
    }

    @PostMapping("/register/doctor")
    public String registerDoctor(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_DOCTOR");
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
