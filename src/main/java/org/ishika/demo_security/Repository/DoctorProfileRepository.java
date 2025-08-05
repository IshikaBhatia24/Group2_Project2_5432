package org.ishika.demo_security.Repository;

import org.ishika.demo_security.model.Appointment;
import org.ishika.demo_security.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {


    List<DoctorProfile> findByUser(User user);
}
