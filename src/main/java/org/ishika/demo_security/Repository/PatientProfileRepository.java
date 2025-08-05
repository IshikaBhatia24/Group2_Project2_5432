package org.ishika.demo_security.Repository;

import org.ishika.demo_security.model.PatientProfile;
import org.ishika.demo_security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    PatientProfile findByUser(User user);
}
