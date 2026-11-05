package com.apnaacademy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id") private Long userId;
    @Column(name = "course_id") private Long courseId;
    @Column(name = "admission_number", length = 50) private String admissionNumber;
    @Column(nullable = false, length = 150) private String name;
    @Column(nullable = false, unique = true, length = 150) private String email;
    @Column(length = 20) private String phone;
    @Column(length = 20) private String gender;
    private LocalDate dob;
    @Column(columnDefinition = "text") private String address;
    @Column(name = "admission_date") private LocalDate admissionDate;
    @Column(length = 30) private String status = "ACTIVE";
    @Column(name = "created_at") private LocalDateTime createdAt;

    public Student() {}
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
