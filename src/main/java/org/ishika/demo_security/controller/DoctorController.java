package org.ishika.demo_security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @GetMapping("/dashboard")
    public String doctorDashboard() {
        // fetch doctor appointment requests, upcoming appointments
        return "doctor-dashboard";
    }
}

