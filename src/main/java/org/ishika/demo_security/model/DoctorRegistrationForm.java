package org.ishika.demo_security.model;

public class DoctorRegistrationForm {



    private User user;
    private DoctorProfile doctorProfile;

    public DoctorProfile getDoctorProfile() {
        return doctorProfile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDoctorProfile(DoctorProfile doctorProfile) {
        this.doctorProfile = doctorProfile;
    }
}
