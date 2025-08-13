package org.ishika.demo_security.controller;

import org.ishika.demo_security.Repository.AppointmentRepository;
import org.ishika.demo_security.Repository.UserRepository;
import org.ishika.demo_security.model.Appointment;
import org.ishika.demo_security.model.AppointmentStatus;
import org.ishika.demo_security.model.User;
import org.ishika.demo_security.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;


    @Autowired
    public DoctorController(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/dashboard")
    public String doctorDashboard(Model model, Principal principal,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        User doctor = userRepository.findByEmail(principal.getName());
        if (doctor == null) {
            return "redirect:/login?error";
        }

        List<Appointment> appointments;

        if (status != null && !status.isEmpty() && date != null) {
            appointments = appointmentRepository.findByDoctorIdAndStatusAndDate(doctor.getId(), AppointmentStatus.valueOf(status), date);
        } else if (status != null && !status.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(doctor.getId(), AppointmentStatus.valueOf(status));
        } else if (date != null) {
            appointments = appointmentRepository.findByDoctorIdAndDate(doctor.getId(), date);
        } else {
            appointments = appointmentRepository.findByDoctorId(doctor.getId());
        }

        // Fetch patient info map: patientId -> patientName
        Map<Long, String> patientNames = new HashMap<>();
        for (Appointment appt : appointments) {
            if (!patientNames.containsKey(appt.getPatientId())) {
                User patient = userRepository.findById(appt.getPatientId()).orElse(null);
                if (patient != null) {
                    patientNames.put(appt.getPatientId(), patient.getFullName());
                } else {
                    patientNames.put(appt.getPatientId(), "Unknown Patient");
                }
            }
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("patientNames", patientNames);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterDate", date != null ? date.toString() : "");

        return "doctor-dashboard";
    }



    @PostMapping("/update-status")
    public String updateAppointmentStatus(@RequestParam Long appointmentId, @RequestParam String status) {
        appointmentService.updateAppointmentStatus(appointmentId, status);
        return "redirect:/doctor/dashboard";
    }


    @PostMapping("/confirm")
    public String confirmAppointment(@RequestParam Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId).orElseThrow();
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt);

        List<Appointment> others = appointmentRepository
                .findByDoctorIdAndDateAndSlotAndStatus(appt.getDoctorId(), appt.getDate(), appt.getSlot(), AppointmentStatus.PENDING);

        for (Appointment other : others) {
            if (!other.getId().equals(appt.getId())) {
                other.setStatus(AppointmentStatus.CANCELLED);
                appointmentRepository.save(other);

                // Send notification email to other.getPatientId()
                // Use your EmailService here
            }
        }
        return "redirect:/doctor/dashboard";
    }


    @GetMapping("/booked-slots/{doctorId}")
    @ResponseBody
    public List<String> getBookedSlots(@PathVariable Long doctorId) {
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndStatusNot(doctorId, AppointmentStatus.CANCELLED);
        return bookedAppointments.stream().map(Appointment::getSlot).collect(Collectors.toList());
    }

    @GetMapping("/calendar")
    public String doctorCalendar(Model model, Principal principal) {
        User doctor = userRepository.findByEmail(principal.getName());
        if (doctor == null) return "redirect:/login?error";
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctor.getId());
        model.addAttribute("appointments", appointments);
        return "doctor-calendar"; // New Thymeleaf template for calendar view
    }

}
