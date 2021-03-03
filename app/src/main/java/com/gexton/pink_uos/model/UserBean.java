package com.gexton.pink_uos.model;

import java.io.File;

public class UserBean {
    public String email, password, password_confirmation, first_name, last_name, roll_no, mobile_no, enroll_year;
    public String father_name, department, emergency_contact;
    public String cnic;
    public File profile_image;

    public UserBean(String email, String password, String password_confirmation, String first_name, String last_name, String roll_no, String mobile_no, String enroll_year, String father_name, String department, String emergency_contact, String cnic, File profile_image) {
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.first_name = first_name;
        this.last_name = last_name;
        this.roll_no = roll_no;
        this.mobile_no = mobile_no;
        this.enroll_year = enroll_year;
        this.father_name = father_name;
        this.department = department;
        this.emergency_contact = emergency_contact;
        this.cnic = cnic;
        this.profile_image = profile_image;
    }
}
