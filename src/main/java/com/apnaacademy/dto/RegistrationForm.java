package com.apnaacademy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {
    @NotBlank @Size(max = 150)
    private String name;
    @NotBlank @Email @Size(max = 150)
    private String email;
    @Size(max = 20)
    private String phone;
    @NotBlank @Size(min = 8, max = 72)
    private String password;
    private Long courseId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}
