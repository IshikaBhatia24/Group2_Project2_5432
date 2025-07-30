package org.ishika.demo_security.controller;


import org.ishika.demo_security.Repository.AppointmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppointmentController {

    private AppointmentRepository appointmentRepository;

    @PostMapping("/cancel")
    @ResponseBody
    public String cancelAppointment(@RequestParam Long doctorId, @RequestParam Long patientId) {

        appointmentRepository.deleteByDoctorIdAndPatientId(doctorId, patientId);
        return "success";
    }

}
