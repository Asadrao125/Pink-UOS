package com.gexton.pink_uos.model;

import java.io.File;

public class LoginResponse {

    public int is_active, is_student, userId;
    public String verified_at, deleted_at, created_at, updated_at;

    public String email, first_name, last_name, roll_no, mobile_no, enroll_year;
    public String father_name, department, emergency_contact;
    public String cnic;
    public String image_url;

    public LoginResponse(int is_active, int is_student, int userId, String verified_at, String deleted_at,
                         String created_at, String updated_at, String email, String first_name, String last_name,
                         String roll_no, String mobile_no, String enroll_year, String father_name, String department,
                         String emergency_contact, String cnic, String image_url) {

        this.is_active = is_active;
        this.is_student = is_student;
        this.userId = userId;
        this.verified_at = verified_at;
        this.deleted_at = deleted_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.roll_no = roll_no;
        this.mobile_no = mobile_no;
        this.enroll_year = enroll_year;
        this.father_name = father_name;
        this.department = department;
        this.emergency_contact = emergency_contact;
        this.cnic = cnic;
        this.image_url = image_url;
    }
}
